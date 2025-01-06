plugins {
  alias(libs.plugins.com.google.devtools.ksp)
  `kotlinspring-convention`
}

version = libs.versions.composeClient.get()

dependencies {
  implementation(projects.meta)
  implementation(projects.core)

  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)
  implementation(libs.org.springframework.springWebMvc)
  implementation(libs.org.springframework.springWebflux)

  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
  kspTest(projects.ksp.kspClient)

  testImplementation(projects.testToolkit)
  testImplementation(projects.depend.dependJackson)

  kspTest(libs.org.babyfish.jimmer.jimmerKsp)
  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
  testImplementation(libs.org.babyfish.jimmer.jimmerSpringBootStarter)
}
