package com.truenine.component.data.common.crawler.selenium;

import com.truenine.component.core.id.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.Closeable;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * selenium执行池
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@Slf4j
public class DriverExecutePool implements Closeable {

  private final Queue<NamedWrapperDriver> driverQueue = new LinkedBlockingQueue<>();
  private final AtomicBoolean isClosed = new AtomicBoolean(false);
  private final int initSize;
  private final AtomicInteger availSize = new AtomicInteger(0);

  private DriverExecutePool(int initSize) {
    this.initSize = initSize;
  }

  public static DriverExecutePool createPool(
    ChromiumDriverOps.DriverType type,
    int count,
    ChromiumDriverOps.Builder builder
  ) {
    List<RemoteWebDriver> ds = new ArrayList<>(10);
    for (int i = 0; i < count; i++) {
      var d = switch (type) {
        case EDGE -> WebDriverCreator.createRipedEdgeDriver(builder);
        case CHROME -> WebDriverCreator.createRipedChromeDriver(builder);
      };
      ds.add(d);
    }
    return createPool(ds.toArray(RemoteWebDriver[]::new));
  }

  public static DriverExecutePool createPool(RemoteWebDriver... drivers) {
    var pool = new DriverExecutePool(drivers.length);
    for (RemoteWebDriver driver : drivers) {
      pool.driverQueue.add(new NamedWrapperDriver(WrappedDriver.wrapper(driver), UUIDGenerator.str(), false));
    }
    pool.changeAvailSize(drivers.length);
    return pool;
  }

  private void changeAvailSize(int availSize) {
    this.availSize.set(availSize);
  }

  public NamedWrapperDriver driver() {
    return driver(Duration.of(5, ChronoUnit.SECONDS));
  }

  NamedWrapperDriver driver(Duration timeout) {
    // 循环等待一个 driverNode
    var clock = Clock.systemDefaultZone();
    var end = clock.instant().plus(timeout);
    while (this.availSize.get() <= 0
      && !this.isClosed.get()) {
      if (end.isBefore(clock.instant())) {
        return null;
      }
    }

    var pulledNode = Objects.requireNonNull(this.driverQueue.remove());
    pulledNode.setUsing(true);
    this.availSize.getAndDecrement();
    return pulledNode;
  }

  public void destroy(NamedWrapperDriver node) {
    node.setUsing(false);
    var handles = node.driver().nativeDriver().getWindowHandles();
    for (String h : handles) {
      log.info("driver 链接 {} 归池", h);
      node.driver().closeTab();
    }
    driverQueue.add(node);
    availSize.incrementAndGet();
  }

  @Override
  @SuppressWarnings("all")
  public void close() throws IOException {
    isClosed.set(true);
    // 等待所有链接归池
    while (driverQueue.size() < this.initSize) ;
    this.driverQueue.forEach(n -> {
      // 是否正在执行任务，直到任务结束
      while (!n.isUsing()) {
        n.driver().nativeDriver().quit();
        n.setUsing(true);
      }
    });

    driverQueue.clear();
  }

  // TODO 处理 driver 的所有错误并建立恢复机制，如果浏览器宕机则拉起一个新的链接并存入
}
