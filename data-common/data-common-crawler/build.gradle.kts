project.version = V.Component.dataCommonCrawler

dependencies {
  api("org.seleniumhq.selenium:selenium-java")
  implementation("com.github.magese:ik-analyzer")
  implementation("com.github.haifengl:smile-math")
  api("com.squareup.okhttp3:okhttp")
  api("io.github.bonigarcia:webdrivermanager")
  api(project(":data-common:data-common-data-extract"))
  implementation(project(":core"))
}
