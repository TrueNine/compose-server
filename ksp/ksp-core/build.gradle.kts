project.version = libs.versions.compose.ksp.core.get()

dependencies {
  api(libs.jakarta.persistence.jakarta.persistence.api)
  api(libs.jakarta.validation.jakarta.validation.api)
  api(libs.org.springframework.data.spring.data.commons)

  api(project(":rds:rds-core"))
  api(project(":core"))
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
