package io.tn.commondata.crawler.selenium;

import com.google.common.annotations.Beta;
import io.tn.core.dev.BetaTest;
import io.tn.core.encrypt.base64.Base64Helper;
import io.tn.core.lang.DTimer;
import io.tn.core.lang.Str;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 对 {@link RemoteWebDriver} 的一层封装
 *
 * @author TrueNine
 * @since 2022-11-15
 */
@Slf4j
public class WrappedDriver {
  private static final Base64Helper BASE64HELPER = Base64Helper.defaultHelper();
  private static final By ROOT_BODY_BY = By.xpath("//body[1]");
  private final By ROOT_HTML_BY = By.xpath("//html[1]");
  private final int DEFAULT_DEEPIN = 10;
  private final RemoteWebDriver driver;
  private final WebDriverWait driverWait;
  private int defaultWindowWidth = 1920;
  private int defaultWindowHeight = 1080;

  private WrappedDriver(RemoteWebDriver driver, long waitMillis) {
    this.driver = driver;
    this.driverWait = new WebDriverWait(driver, Duration.of(waitMillis, ChronoUnit.MILLIS));
    this.driverWait
      .ignoring(NullPointerException.class)
      .ignoring(StaleElementReferenceException.class)
      .ignoring(NoSuchElementException.class);
  }

  public static WrappedDriver wrapper(RemoteWebDriver driver) {
    return wrapper(driver, 3 * 1000);
  }

  public static WrappedDriver wrapper(RemoteWebDriver driver, long waitMillis) {
    return new WrappedDriver(driver, waitMillis);
  }

  public <T> T deepinCatchElementException(Supplier<T> exec, int deepin, int currentDeepin) {
    if (currentDeepin > deepin) {
      log.warn("超出最大递归深度 当前深度 {}, 最大深度 {}", currentDeepin, deepin);
      return null;
    }
    try {
      return exec.get();
    } catch (StaleElementReferenceException
             | NoSuchElementException e) {
      DTimer.sleepMillis(100);
      deepinCatchElementException(exec, deepin, currentDeepin + 1);
    }
    return null;
  }

  public <T> T deepinCatchElementException(Supplier<T> exec) {
    return deepinCatchElementException(exec, DEFAULT_DEEPIN, 0);
  }

  public void quit() {
    nativeDriver().quit();
  }

  public RemoteWebDriver nativeDriver() {
    return driver;
  }

  public List<WebElement> findElementsByXpath(String by) {
    return findElements(By.xpath(by));
  }

  public List<WebElement> findElements(By by) {
    return deepinCatchElementException(() -> until(d -> d.findElements(by)));
  }

  public WebElement findElementByXpath(String by) {
    return findElement(By.xpath(by));
  }

  public WebElement findElement(By by) {
    return deepinCatchElementException(() -> until(d -> d.findElement(by)));
  }

  public <V> V until(Function<? super WebDriver, V> waitFor) {
    return driverWait.until(waitFor);
  }

  public FluentWait<WebDriver> driverWait() {
    return driverWait;
  }

  public String allHtml() {
    return elementHtml(ROOT_HTML_BY);
  }

  public String allBody() {
    return elementHtml(ROOT_BODY_BY);
  }

  public Dimension htmlSize() {
    return getElementSize(ROOT_HTML_BY);
  }

  public WrappedDriver setDefaultWindowSize(int width, int height) {
    defaultWindowWidth = width;
    defaultWindowHeight = height;
    return this;
  }

  public Dimension elementSize(By by) {
    return getElementSize(by);
  }

  public void resetWindowSize(int width, int height) {
    driver.manage().window().setSize(new Dimension(width, height));
  }

  public void resetWindowSize() {
    resetWindowSize(defaultWindowWidth, defaultWindowHeight);
  }

  public void findAndExecute(By by, Consumer<WebElement> exec) {
    deepinCatchElementException(() -> {
      var r = driverWait.until(d -> d.findElement(by));
      exec.accept(r);
      return r;
    });
  }

  public void findsAndExecute(By by, Consumer<List<WebElement>> exec) {

    deepinCatchElementException(() -> {
      var r = driverWait.until(d -> d.findElements(by));
      exec.accept(r);
      return r;
    });
  }

  public Dimension getElementSize(By by) {
    var element = deepinCatchElementException(() -> driverWait.until(d -> d.findElement(by)));
    var bodyWidth = (Long) driver.executeScript("""
      const dom = arguments[0];
      return Math.max(
        dom.scrollWidth,
        dom.clientWidth,
        dom.offsetWidth
      )
      """, element);
    var bodyHeight = (Long) driver.executeScript("""
              const dom = arguments[0];
              return Math.max(
                dom.scrollHeight,
                dom.clientHeight,
                dom.offsetHeight
              );
      """, element);
    return new Dimension(Math.toIntExact(bodyWidth), Math.toIntExact(bodyHeight));
  }

  public void scrollToBottom() {
    var range = htmlSize();
    driver.executeScript("window.scrollTo(0,arguments[0])", range.getHeight());
  }

  public WebElement rootHtmlElement() {
    return deepinCatchElementException(() -> driverWait.until(d -> d.findElement(ROOT_HTML_BY)));
  }

  @Beta
  @BetaTest
  public byte[] screenshotFromBody() {
    var bodySize = htmlSize();
    var windowSize = driver.manage().window().getSize();
    var widthScrollBarDifference = windowSize.getWidth() - bodySize.getWidth();

    ((ChromiumDriver) driver).executeCdpCommand("Emulation.setDeviceMetricsOverride", Map.of(
      "mobile", false,
      "width", bodySize.getWidth() - widthScrollBarDifference,
      "height", bodySize.getHeight(),
      "deviceScaleFactor", 1
    ));
    var base64Screenshot = (String) ((ChromiumDriver) driver).executeCdpCommand("Page.captureScreenshot",
      Map.of("fromSurface", true)
    ).get("data");
    resetWindowSize();
    return BASE64HELPER.decodeToByte(base64Screenshot);
  }

  @BetaTest
  public byte[] screenshotFromElement(By by) {
    var element = findElement(by);
    var screenImageByte = screenshotFromBody();
    var elementSize = element.getSize();
    var elementLocation = element.getLocation();
    try (var byteArrayInputStream = new ByteArrayInputStream(screenImageByte);
         var byteArrayOutputStream = new ByteArrayOutputStream()) {
      var bufferedImage = ImageIO.read(byteArrayInputStream);
      var croppedImage = bufferedImage.getSubimage(
        elementLocation.getX(),
        elementLocation.getY(),
        elementSize.getWidth(),
        elementSize.getHeight()
      );
      ImageIO.write(croppedImage, "png", byteArrayOutputStream);
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      resetWindowSize();
    }
  }

  public String elementHtml(By by) {
    var element = findElement(by);
    return element.getAttribute("outerHTML");
  }

  public String elementText(WebElement element) {
    if (element.isDisplayed()) {
      return element.getText();
    } else {
      return element.getAttribute("textContent");
    }
  }


  public BrowserStore getStore() {
    var l = driver.executeScript("""
      const l = Object.keys(window.localStorage);
      const result = [];
      l.forEach(key => {
        result.push({[key]:window.localStorage.getItem(key)});
      })
      return result;
      """);
    var s = driver.executeScript("""
      const l = Object.keys(window.sessionStorage);
      const result = [];
      l.forEach(key => {
        result.push({[key]:window.sessionStorage.getItem(key)});
      })
      return result;
      """);
    List<Map<String, Object>> localStorage = (List<Map<String, Object>>) l;
    List<Map<String, Object>> sessionStorage = (List<Map<String, Object>>) s;
    Set<Cookie> cookies = driver.manage().getCookies();
    return new io.tn.commondata.crawler.selenium.BrowserStore()
      .setLocalStorage(localStorage)
      .setSessionStorage(sessionStorage)
      .setCookies(cookies);
  }


  public void resetStore(BrowserStore store) {
    if (Objects.isNull(store)) {
      return;
    }

    if (!Objects.isNull(store.getLocalStorage()) || !store.getLocalStorage().isEmpty()) {
      var l = driver.executeScript("""
        const l =arguments[0];
        l.forEach(i=> {
           for (const k in i) {
             window.localStorage.setItem(k, i[k]);
           }
        });
        """, store.getLocalStorage());
    }

    if (!Objects.isNull(store.getSessionStorage()) || store.getSessionStorage().isEmpty()) {
      var s = driver.executeScript("""
        const s =arguments[0];
        s.forEach(i=> {
           for (const k in i) {
             window.sessionStorage.setItem(k, i[k]);
           }
        });
        """, store.getSessionStorage());
    }

    if (!Objects.isNull(store.getCookies())) {
      driver.manage().deleteAllCookies();
      store.getCookies().forEach(c -> {
        driver.manage().addCookie(c);
      });
    }
  }

  public void executeLastTabTask(
    Consumer<WrappedDriver> activeNewTabTask,
    Consumer<WrappedDriver> newTabTask) {
    var oldTab = driver.getWindowHandle();
    activeNewTabTask.accept(this);
    var tabs =
      driver.getWindowHandles().stream()
        .filter(handle -> !handle.equals(oldTab)).toList();
    var newTab = tabs.get(tabs.size() - 1);
    driver.switchTo().window(newTab);
    newTabTask.accept(this);
    closeTab();
    driver.switchTo().window(oldTab);
  }

  public Document toDocument() {
    return Jsoup.parse(allHtml());
  }

  public void closeTab() {
    try {
      driver.executeScript("window.location.href='about:blank';window.close();");
    } catch (Exception e) {
      log.debug("关闭进程错误");
    } finally {
      log.debug("driver {} 已经执行虚关闭", this);
    }
  }

  public void waitFor(By by, Function<List<WebElement>, Boolean> expr) {
    driverWait.until(d -> {
      var r = expr.apply(findElements(by));
      return r;
    });
  }

  public void waitFor(By by, By by2, BiFunction<List<WebElement>, List<WebElement>, Boolean> expr) {
    until(d -> ExpectedConditions.and(
      ExpectedConditions.presenceOfElementLocated(by),
      ExpectedConditions.presenceOfElementLocated(by2)
    ));
    Boolean a = until(d -> expr.apply(
      findElements(by).stream().filter(Objects::nonNull).toList(),
      findElements(by2).stream().filter(Objects::nonNull).toList()
    ));
    System.out.println("a = " + a);
  }

  public void waitFor(By by) {
    findElements(by);
  }

  public <D> void updateNextByHtml(Function<WrappedDriver, D> loopExec,
                                   Consumer<D> saveExec,
                                   Function<WrappedDriver, Boolean> next) {
    AtomicBoolean ies = new AtomicBoolean(false);
    do {
      log.warn("获取数据");
      D data = null;
      try {
        data = deepinCatchElementException(
          () -> until(d -> loopExec.apply(this))
        );
      } catch (Throwable ex) {
        log.warn("update更新异常", ex);
        data = deepinCatchElementException(
          () -> until(d -> loopExec.apply(this))
        );
      }

      saveExec.accept(data);
      var oldHtml = allBody();
      ies.set(next.apply(this));

      // 等待新的 刷新
      var ato = false;
      while (!ato) {
        ato = until(d -> {
          var newHtml = allBody();
          var a = !ies.get();
          var b = !oldHtml.equals(newHtml);
          log.debug("\nold = \n{}\n new = {}\n", Str.omit(oldHtml), Str.omit(newHtml));
          log.warn("a || b {}", a || b);
          return a || b;
        });
      }
    } while (ies.get());
  }

  public String currentUrl() {
    return nativeDriver().getCurrentUrl();
  }
}
