version = libs.versions.composeRdsJimmer.get()

plugins {
  idea
  alias(libs.plugins.tech.argonariod.gradlePluginJimmer)
  alias(libs.plugins.com.google.devtools.ksp)
}



jimmer {
  version = libs.versions.jimmer.get()
}

dependencies {
  implementation(libs.org.babyfish.jimmer.jimmerSpringBootStarter)
  implementation(libs.org.springframework.boot.springBootAutoconfigure)
  kapt(libs.org.springframework.springBootConfigurationProcessor)

  implementation(project(":core"))
  implementation(project(":rds:rds-core"))

  ksp(libs.org.babyfish.jimmer.jimmerKsp)
  testImplementation(libs.org.babyfish.jimmer.jimmerSpringBootStarter)
  testImplementation(project(":test-toolkit"))
  testImplementation(libs.org.springframework.boot.springBootStarterDataJdbc)
  testImplementation(libs.org.springframework.boot.springBootStarterJdbc)
  testImplementation(libs.org.flywaydb.flywayCore)
  testImplementation(project(":rds:rds-migration-h2"))
}

/*
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
*/
