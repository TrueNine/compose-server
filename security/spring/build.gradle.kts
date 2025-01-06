plugins {
  `kotlinspring-convention`
}

version = libs.versions.composeSecuritySpring.get()

dependencies {
  api(libs.org.springframework.boot.springBootStarterSecurity)

  implementation(libs.cn.hutool.hutoolCaptcha)
  implementation(libs.com.auth0.javaJwt)

  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)

  implementation(libs.org.springframework.springWebMvc)

  implementation(libs.org.owasp.antisamy.antisamy) {
    exclude(group = "org.htmlunit", module = "neko-htmlunit")
    implementation(libs.org.htmlunit.nekoHtmlunit)
  }

  //implementation(project(":depend:depend-http-exchange"))
  // TODO 剥离 web模块
  implementation(projects.depend.dependServlet)
  implementation(projects.security.securityCrypto)
  implementation(projects.core)

  testImplementation(projects.testToolkit)
}


