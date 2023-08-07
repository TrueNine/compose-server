package net.yan100.compose.plugin.versions

import org.gradle.api.artifacts.Dependency

object Crawler {
  // https://mvnrepository.com/artifact/io.github.bonigarcia/webdrivermanager
  const val webDriverManager = "5.4.1"

  // https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java
  const val seleniumJava = "4.11.0"

  // 用于计算海明距离
  // https://mvnrepository.com/artifact/com.github.haifengl/smile-math
  const val smileMath = "2.6.0"

  // 分词器，用于爬虫框架
  // https://mvnrepository.com/artifact/com.github.magese/ik-analyzer
  const val ikAnalyzer = "8.5.0"

  // https://mvnrepository.com/artifact/org.jsoup/jsoup
  const val jsoup = "1.16.1"

  /**
   * ## playwright
   * [maven](https://repo1.maven.org/maven2/com/microsoft/playwright/playwright/)
   */
  const val playwright = "1.36.0"
}
