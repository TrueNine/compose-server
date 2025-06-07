plugins {
  `kotlinspring-convention`
}

version = libs.versions.compose.pay.get()

dependencies {
  api(libs.com.github.wechatpay.apiv3.wechatpay.java)

  api(projects.shared)
  implementation(projects.depend.dependHttpExchange)
  implementation(libs.org.springframework.boot.spring.boot.starter.webflux)
  implementation(projects.security.securityOauth2)
  implementation(projects.security.securityCrypto)

  testImplementation(projects.testtoolkit)
}
