plugins {
  `kotlinspring-convention`
}

version = libs.versions.composeDependSpringdocOpenapi.get()

dependencies {
  api(libs.io.swagger.core.v3.swaggerAnnotationsJakarta)

  implementation(projects.core)
  implementation(libs.org.springdoc.springdocOpenapiStarterWebmvcUi)

  testImplementation(projects.testToolkit)
  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
}
