plugins { `kotlinspring-convention` }

project.version = libs.versions.compose.security.oauth2.get()

dependencies {
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)

  implementation(projects.core)
  implementation(projects.depend.dependHttpExchange)
  implementation(libs.org.springframework.boot.spring.boot.starter.webflux)

  implementation(projects.security.securityCrypto)
  implementation(libs.org.springframework.security.spring.security.core)

  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
  testImplementation(projects.testtoolkit)
}
