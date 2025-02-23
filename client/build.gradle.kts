plugins {
  alias(libs.plugins.com.google.devtools.ksp)
  `kotlinspring-convention`
}

version = libs.versions.compose.client.get()

dependencies {
  implementation(projects.meta)
  implementation(projects.core)

  implementation(libs.com.fasterxml.jackson.core.jackson.databind)
  implementation(libs.org.springframework.spring.webmvc)
  implementation(libs.org.springframework.spring.webflux)

  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
  kspTest(projects.ksp.kspClient)

  testImplementation(projects.testtoolkit)
  testImplementation(projects.depend.dependJackson)

  kspTest(libs.org.babyfish.jimmer.jimmer.ksp)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
  testImplementation(libs.org.babyfish.jimmer.jimmer.spring.boot.starter)
}
