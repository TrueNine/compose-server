package net.yan100.compose.plugin.versions

object Crawler {
  // https://mvnrepository.com/artifact/io.github.bonigarcia/webdrivermanager
  val webDriverManager = "5.5.3"

  // https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java
  const val seleniumJava = "4.12.1"

  // https://mvnrepository.com/artifact/org.jsoup/jsoup
  const val jsoup = "1.16.1"

  /**
   * ## playwright
   * @see [maven](https://mvnrepository.com/artifact/com.microsoft.playwright/playwright)
   */
  const val playwright = "1.38.0"

  /**
   * # 顶替 antisamy
   * org.htmlunit:neko-htmlunit
   * @see [maven](https://mvnrepository.com/artifact/org.htmlunit/neko-htmlunit)
   */
  const val nekohtmlUnit = "3.5.0"
}
