plugins {
  alias(libs.plugins.com.google.devtools.ksp)
}

version = libs.versions.composeClient.get()

dependencies {
  implementation(libs.org.springframework.boot.springBootAutoconfigure)
  kapt(libs.org.springframework.springBootConfigurationProcessor)

  implementation(project(":meta"))
  implementation(project(":core"))
  implementation(project(":ksp:ksp-tspoet"))

  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)
  implementation(libs.org.springframework.springWebMvc)
  implementation(libs.org.springframework.springWebflux)

  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
  kspTest(project(":ksp:ksp-client"))
  kspTest(libs.org.babyfish.jimmer.jimmerKsp)
  testImplementation(project(":test-toolkit"))
  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
  testImplementation(libs.org.babyfish.jimmer.jimmerSpringBootStarter)
  testImplementation(project(":depend:depend-jackson"))
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()
      from(components["java"])
    }
  }
}

signing {
  useGpgCmd()
  sign(publishing.publications["maven"])
}
