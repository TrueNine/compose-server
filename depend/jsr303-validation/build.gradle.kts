version = libs.versions.composeDependJsr303Validation.get()

plugins { alias(libs.plugins.com.google.devtools.ksp) }

dependencies {
  testImplementation(project(":test-toolkit"))
  testImplementation(project(":rds:rds-core"))

  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
  testImplementation(libs.org.springframework.boot.springBootStarterDataJpa)
  implementation(libs.org.springframework.boot.springBootStarterValidation)

  implementation(project(":core"))
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
