plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
WeChat Official Account Platform SDK implementation providing comprehensive APIs for WeChat public platform integration.
Includes authentication, user information retrieval, access token management, and JSAPI signature generation capabilities.
"""
    .trimIndent()

dependencies {
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)

  api(projects.shared)
  implementation(projects.depend.dependHttpExchange)
  implementation(projects.depend.dependJackson)
  implementation(libs.org.springframework.boot.spring.boot.starter.webflux)

  implementation(projects.security.securityCrypto)
  implementation(libs.org.springframework.security.spring.security.core)

  testImplementation(libs.org.springframework.boot.spring.boot.starter.webflux)
  testImplementation(projects.testtoolkit)
}
