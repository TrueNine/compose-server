plugins { `kotlinspring-convention` }

version = libs.versions.compose.depend.servlet.get()

dependencies {
  api(libs.org.springframework.boot.spring.boot.starter.web)
  api(libs.jakarta.servlet.jakarta.servlet.api)

  implementation(libs.org.springframework.boot.spring.boot.starter.websocket)
  implementation(libs.org.springframework.boot.spring.boot.autoconfigure)

  implementation(projects.core)
  implementation(projects.security.securityCrypto)
  testImplementation(projects.testtoolkit)
}
