project.version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.spring.boot.security)
  api(libs.util.hutool.captcha)
  api(libs.security.auth0Jwt)
  implementation(libs.spring.webmvc)
  implementation(libs.security.antisamy) {
    //exclude(group = "org.htmlunit:neko-htmlunit", module = "neko-htmlunit")
    //implementation(libs.cralwer.nekohtml)
  }
  implementation(project(":core"))
}




