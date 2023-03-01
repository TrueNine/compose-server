package com.truenine.component.data.common.crawler.selenium;


import io.github.bonigarcia.wdm.WebDriverManager;
import io.tn.core.api.http.mock.UserAgents;
import io.tn.core.dev.BetaTest;
import io.tn.core.lang.ResourcesLocator;
import io.tn.core.lang.Str;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverLogLevel;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class WebDriverCreator {
  private static final String CHROME_DRIVER_KEY = "webdriver.chrome.driver";
  private static final String EDGE_DRIVER_KEY = "webdriver.edge.driver";

  /**
   * 创建一个 服务器端的 chromeDriver
   * <br/>
   * 注意：此种 driver 会 默认使用无头配置运行
   *
   * @param url url
   * @return {@link RemoteWebDriver}
   */
  public static RemoteWebDriver createRemoteChromeDriver(URL url) {
    var opt = new DesiredCapabilities("chrome", "", Platform.ANY);
    var chromeOpt = new ChromeOptions();
    chromeOpt.addArguments("--headless");
    chromeOpt.merge(opt);
    return new RemoteWebDriver(url, chromeOpt);
  }

  /**
   * 获得一个 edge 的 driver
   *
   * @param builder 构建器
   * @return {@link RemoteWebDriver}
   */
  @BetaTest
  public static RemoteWebDriver createRipedEdgeDriver(ChromiumDriverOps.Builder builder) {
    var ops = checkOptionsAndCreateDefault(
      builder.build(new ChromiumDriverOps())
    );
    return configChromium(
      ChromiumDriverOps.DriverType.EDGE,
      ops
    );
  }

  /**
   * 创建 一个 调试好的 chrome 驱动
   *
   * @param builder {@link ChromiumDriverOps}
   * @return {@link RemoteWebDriver}
   */
  public static RemoteWebDriver createRipedChromeDriver(ChromiumDriverOps.Builder builder) {
    var ops = checkOptionsAndCreateDefault(
      builder.build(new ChromiumDriverOps())
    );
    return configChromium(
      ChromiumDriverOps.DriverType.CHROME,
      ops
    );
  }

  private static ChromiumDriverOps checkOptionsAndCreateDefault(ChromiumDriverOps o) {
    if (Objects.nonNull(o)) {
      var path = o.getDownloadPath();
      if (Str.nonText(path)) {
        o.setDownloadPath(ResourcesLocator.getGenerateDir().getPath());
      }
      return o;
    }
    return new ChromiumDriverOps().prod();
  }

  private static RemoteWebDriver configChromium(
    ChromiumDriverOps.DriverType type,
    ChromiumDriverOps cdo) {
    ChromiumDriver driver = switch (type) {
      case EDGE -> {
        WebDriverManager.edgedriver().setup();
        yield new EdgeDriver(
          configEdgeOptions(
            new EdgeOptions(),
            cdo.isHeadless()));
      }
      default -> {
        WebDriverManager.chromedriver().driverVersion("107").setup();
        yield new ChromeDriver(
          configChromeOptions(
            new ChromeOptions(),
            cdo.isHeadless()
          )
        );
      }
    };

    // 去除 js context 的 webdriver
    driver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", Map.of("source", """
      Object.defineProperty(navigator,'webdriver',
          {
              get: () => undefined
          }
      );
      """));
    driver.setLogLevel(Level.OFF);
    driver.manage().window().maximize();
    return driver;
  }

  private static ChromeOptions configChromeOptions(ChromeOptions options,
                                                   boolean headless) {
    if (headless) {
      options.addArguments("--headless");
    }

    options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
    options.addArguments("--incognito");
    options.addArguments("--window-size=1920,1080");
    options.addArguments("--start-maximized");

    // chrome 特别参数
    options.setLogLevel(ChromeDriverLogLevel.OFF);
    options.addArguments("User-Agent=" + UserAgents.CHROME_WIN_103.val());
    return options;
  }

  private static EdgeOptions configEdgeOptions(EdgeOptions options, boolean headless) {
    if (headless) {
      options.addArguments("--headless");
    }
    options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
    options.addArguments("--incognito");
    options.addArguments("--window-size=1920,1080");
    options.addArguments("--start-maximized");

    // edge 特别参数
    options.addArguments("User-Agent=" + UserAgents.EDGE_WIN_106.val());
    return options;
  }
}
