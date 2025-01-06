plugins {
  `kotlinspring-convention`
}

version = libs.versions.composePay.get()

dependencies {
  api(libs.com.github.wechatpayApiv3.wechatpayJava)

  implementation(projects.core)
  implementation(projects.depend.dependHttpExchange)
  implementation(libs.org.springframework.boot.springBootStarterWebflux)
  implementation(projects.security.securityOauth2)
  implementation(projects.security.securityCrypto)

  testImplementation(projects.testToolkit)
}
