plugins { `kotlinspring-convention` }

version = libs.versions.compose.security.spring.get()

dependencies {
  api(libs.org.springframework.boot.spring.boot.starter.security)

  implementation(libs.cn.hutool.hutool.captcha)
  implementation(libs.com.auth0.java.jwt)

  implementation(libs.com.fasterxml.jackson.core.jackson.databind)

  implementation(libs.org.springframework.spring.webmvc)

  implementation(libs.org.owasp.antisamy.antisamy) {
    exclude(group = "org.htmlunit", module = "neko-htmlunit")
    implementation(libs.org.htmlunit.neko.htmlunit)
  }

  // implementation(project(":depend:depend-http-exchange"))
  // TODO 剥离 web模块
  implementation(projects.depend.dependServlet)
  implementation(projects.security.securityCrypto)
  implementation(projects.core)

  testImplementation(projects.testtoolkit)
}
