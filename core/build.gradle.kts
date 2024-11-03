version = libs.versions.compose.core.get()

dependencies {
  api(libs.com.fasterxml.jackson.core.jacksonAnnotations)
  api(libs.jakarta.validation.jakartaValidationApi)
  api(libs.jakarta.persistence.jakartaPersistenceApi)
  api(libs.jakarta.annotation.jakartaAnnotationApi)
  api(libs.jakarta.servlet.jakartaServletApi)
  api(libs.io.swagger.core.v3.swaggerAnnotationsJakarta)
  api(libs.org.slf4j.slf4jApi)

  testImplementation(project(":test-toolkit"))
  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
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
