plugins {
  `kotlinspring-convention`
}

version = libs.versions.composeDependServlet.get()

dependencies {
  api(libs.org.springframework.boot.springBootStarterWeb)
  api(libs.jakarta.servlet.jakartaServletApi)

  implementation(libs.org.springframework.boot.springBootStarterWebsocket)
  implementation(libs.org.springframework.boot.springBootAutoconfigure)

  implementation(projects.core)
  implementation(projects.security.securityCrypto)
  testImplementation(projects.testToolkit)
}
