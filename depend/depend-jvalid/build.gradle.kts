version = libs.versions.compose.asProvider().get()

plugins { alias(libs.plugins.ktKsp) }

dependencies {
  testImplementation(project(":test-toolkit"))
  testImplementation(project(":rds:rds-core"))

  testImplementation(libs.spring.boot.web)
  testImplementation(libs.spring.boot.dataJpa)

  implementation(libs.spring.boot.validation)
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
