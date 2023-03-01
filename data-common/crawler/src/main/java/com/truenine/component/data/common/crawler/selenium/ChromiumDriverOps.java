package com.truenine.component.data.common.crawler.selenium;

import io.tn.core.lang.ResourcesLocator;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 硒铬驱动程序选项
 * 不传参直接使用默认参数为正常
 * dev 为显示窗口，但其他隐藏
 * prod 为 无头模式运行
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@Data
@Accessors(chain = true)
public class ChromiumDriverOps {
  private boolean headless = false;
  private boolean noTrace = false;
  private boolean maxSize = true;
  private boolean hiddenTestBar = false;
  private String chromiumDriverPath = null;
  private String userAgent = null;
  private String downloadPath = ResourcesLocator.getTempDir().getAbsolutePath();

  public ChromiumDriverOps dev() {
    return new ChromiumDriverOps().setHeadless(false)
      .setNoTrace(true)
      .setMaxSize(true)
      .setHiddenTestBar(true);
  }

  public ChromiumDriverOps prod() {
    return new ChromiumDriverOps().setHeadless(true)
      .setNoTrace(true)
      .setMaxSize(true)
      .setHiddenTestBar(true);
  }

  public enum DriverType {
    /**
     * edge
     */
    EDGE,
    /**
     * chrome
     */
    CHROME
  }

  /**
   * 构造接口
   *
   * @author TrueNine
   * @since 2022-10-29
   */
  @FunctionalInterface
  public interface Builder {

    /**
     * 构建器
     * 用于构建一个 chromium 的配装函数
     *
     * @param defaultOptions 默认选项
     * @return {@link ChromiumDriverOps}
     */
    ChromiumDriverOps build(ChromiumDriverOps defaultOptions);
  }
}
