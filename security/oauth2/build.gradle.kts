plugins {
  `kotlinspring-convention`
}

project.version = libs.versions.composeSecurityOauth2.get()

dependencies {
  implementation(libs.org.jetbrains.kotlinx.kotlinxCoroutinesCore)

  implementation(projects.core)
  implementation(projects.depend.dependHttpExchange)
  implementation(libs.org.springframework.boot.springBootStarterWebflux)

  implementation(projects.security.securityCrypto)
  implementation(libs.org.springframework.security.springSecurityCore)

  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
  testImplementation(projects.testToolkit)
}
