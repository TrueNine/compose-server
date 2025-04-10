plugins { `kotlinspring-convention` }

version = libs.versions.compose.depend.get()

dependencies {
  api(libs.org.springframework.boot.spring.boot.starter.web)
  api(libs.jakarta.servlet.jakarta.servlet.api)

  implementation(libs.org.springframework.boot.spring.boot.starter.websocket)
  implementation(libs.org.springframework.boot.spring.boot.autoconfigure)

  api(projects.shared)
  implementation(projects.security.securityCrypto)
  testImplementation(projects.testtoolkit)
}
