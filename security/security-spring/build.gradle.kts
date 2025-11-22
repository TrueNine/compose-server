plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Spring Security integration providing authentication, authorization, and security configurations.
  Includes JWT token handling, CAPTCHA support, XSS protection, and comprehensive security filters.
  """
    .trimIndent()

dependencies {
  api(libs.org.springframework.boot.spring.boot.starter.security)

  implementation(libs.cn.hutool.hutool.captcha)
  implementation(libs.com.auth0.java.jwt)

  implementation(libs.tools.jackson.core.jackson.databind)

  implementation(libs.org.springframework.spring.webmvc)

  implementation(libs.org.owasp.antisamy.antisamy) { exclude(group = "org.htmlunit", module = "neko-htmlunit") }
  implementation(libs.org.htmlunit.neko.htmlunit)

  // implementation(project(":depend:depend-http-exchange"))
  // TODO extract web module
  implementation(projects.depend.dependServlet)
  implementation(projects.security.securityCrypto)
  api(projects.shared)

  testImplementation(projects.testtoolkit.testtoolkitSpringmvc)
}
