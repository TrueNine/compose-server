plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Servlet API integration and web application utilities for Spring Boot applications.
  Provides servlet containers, WebSocket support, and web application auto-configuration.
  """
    .trimIndent()

dependencies {
  api(libs.org.springframework.boot.spring.boot.starter.web)
  api(libs.jakarta.servlet.jakarta.servlet.api)

  implementation(libs.org.springframework.boot.spring.boot.starter.websocket)
  implementation(libs.org.springframework.boot.spring.boot.autoconfigure)

  api(projects.shared)
  implementation(projects.security.securityCrypto)

  testImplementation(projects.testtoolkit.testtoolkitSpringmvc)
}
