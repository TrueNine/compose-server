version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.net.okhttp3)
  api(libs.crawler.jsoup)
  api(libs.crawler.supercsv)
  api(libs.util.easyexcel) {
    exclude("org.apache.commons", "commons-compress")
    implementation(libs.apache.commonsCompress)
  }
  implementation(project(":core"))
  implementation(project(":depend:depend-web-client"))
}
