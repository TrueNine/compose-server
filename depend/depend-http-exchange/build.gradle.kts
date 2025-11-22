plugins {
  id("buildlogic.kotlin-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  HTTP client utilities and exchange interfaces for reactive and traditional web service communication.
  Provides Spring WebFlux HTTP client support, Netty integration, and JSON processing capabilities.
  """
    .trimIndent()

dependencies {
  api(projects.shared)

  implementation(libs.org.springframework.spring.core)
  implementation(libs.tools.jackson.core.jackson.databind)
  implementation(libs.org.springframework.spring.web)
  implementation(libs.io.netty.netty.handler)
  implementation(libs.org.springframework.spring.webflux)

  testImplementation(libs.org.springframework.boot.spring.boot.starter.webflux)
  testImplementation(projects.testtoolkit.testtoolkitShared)
}
