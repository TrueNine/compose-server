plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  WeChat Pay integration implementation.
  Provides WeChat Pay V3 API integration, payment processing, and automated configuration for WeChat payment services.
  """
    .trimIndent()

dependencies {
  implementation(libs.tools.jackson.core.jackson.databind)
  api(libs.com.github.wechatpay.apiv3.wechatpay.java)

  api(projects.pay.payShared)
  api(projects.shared)

  implementation(projects.depend.dependHttpExchange)
  implementation(projects.security.securityOauth2)
  implementation(projects.security.securityCrypto)
  implementation(libs.org.springframework.boot.spring.boot.starter.webflux)

  testImplementation(projects.testtoolkit.testtoolkitShared)
}
