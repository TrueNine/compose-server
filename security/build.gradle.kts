project.version = libs.versions.compose.get()

dependencies {
  api(libs.spring.boot.security)
  implementation(libs.util.hutoolCaptcha)
  implementation(libs.security.auth0Jwt)
  implementation(libs.jakarta.servletApi)
  implementation(libs.spring.webmvc)
  implementation(libs.security.antisamy) {
    // exclude(group = "org.htmlunit:neko-htmlunit", module = "neko-htmlunit")
    // implementation(libs.crawler.nekohtml)
  }
  implementation(project(":core"))
}
