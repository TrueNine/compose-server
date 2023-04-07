project.version = V.Project.dataCommonCrawler

dependencies {
  api("org.seleniumhq.selenium:selenium-java:${V.Driver.selenium}")
  implementation("com.github.magese:ik-analyzer:${V.Util.ikAnalyzer}")
  implementation("com.github.haifengl:smile-math:${V.Util.smileMath}")
  api("com.squareup.okhttp3:okhttp:${V.Http.okhttp3}")
  api("io.github.bonigarcia:webdrivermanager:${V.Driver.webDriverManager}")
  api(project(":data-common:data-common-data-extract"))
  implementation(project(":core"))
}
