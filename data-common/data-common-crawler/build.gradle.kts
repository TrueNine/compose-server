import net.yan100.compose.plugin.V

project.version = V.Compose.dataCommonCrawler

dependencies {
  api("org.seleniumhq.selenium:selenium-java:${V.Driver.seleniumJava}")
  api("io.github.bonigarcia:webdrivermanager:${V.Driver.webDriverManager}")
  api(project(":data-common:data-common-data-extract"))
  implementation("com.github.magese:ik-analyzer:${V.Util.ikAnalyzer}")
  implementation("com.github.haifengl:smile-math:${V.Util.smileMath}")
  implementation(project(":core"))
}
