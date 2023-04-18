package com.truenine.component.datacommon.dataextract.common.crawler.selenium;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * selenium 静态工具类
 *
 * @author TrueNine
 * @since 2023-01-02
 */
public class SeleniumTool {
  public static void resetWindowSize(RemoteWebDriver driver, int width, int height) {
    driver.manage().window().setSize(new Dimension(width, height));
  }

  public static void resetWindowSize(RemoteWebDriver driver) {
    resetWindowSize(driver, 1920, 1080);
  }
}
