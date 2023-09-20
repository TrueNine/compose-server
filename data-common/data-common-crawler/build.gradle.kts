import net.yan100.compose.plugin.V

version = libs.versions.compose.datacommon.cralwer.get()

dependencies {
  api("org.seleniumhq.selenium:selenium-java:${V.Crawler.seleniumJava}")
  api("io.github.bonigarcia:webdrivermanager:${V.Crawler.webDriverManager}")
  api("com.microsoft.playwright:playwright:${V.Crawler.playwright}")
  api(project(":data-common:data-common-data-extract"))
  implementation("com.github.magese:ik-analyzer:${V.Crawler.ikAnalyzer}")
  implementation("com.github.haifengl:smile-math:${V.Crawler.smileMath}")
  implementation(project(":core"))
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
