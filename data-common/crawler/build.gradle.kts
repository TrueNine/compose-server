project.version = V.Project.crawler

dependencies {
  api("org.jsoup:jsoup:${V.Util.jsoup}")
  api("org.seleniumhq.selenium:selenium-java:${V.Driver.selenium}")
  implementation("com.github.magese:ik-analyzer:${V.Util.ikAnalyzer}")
  implementation("com.github.haifengl:smile-math:${V.Util.smileMath}")
  api("com.squareup.okhttp3:okhttp:${V.Http.okhttp3}")
  api("com.alibaba:easyexcel:${V.Office.easyExcel}")
  api("io.github.bonigarcia:webdrivermanager:${V.Driver.webDriverManager}")
  implementation(project(":core"))
}
