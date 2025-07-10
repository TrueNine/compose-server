plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
Payment processing integration supporting multiple payment providers including WeChat Pay.
Provides secure payment gateway integration, transaction handling, and payment verification capabilities.
"""
    .trimIndent()

dependencies {
  api(libs.com.github.wechatpay.apiv3.wechatpay.java)

  api(projects.shared)
  implementation(projects.depend.dependHttpExchange)
  implementation(libs.org.springframework.boot.spring.boot.starter.webflux)
  implementation(projects.security.securityOauth2)
  implementation(projects.security.securityCrypto)

  testImplementation(projects.testtoolkit)
}
