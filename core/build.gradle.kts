version = libs.versions.composeCore.get()

dependencies {
  implementation(libs.org.springframework.boot.springBootAutoconfigure)
  kapt(libs.org.springframework.springBootConfigurationProcessor)

  api(libs.com.fasterxml.jackson.core.jacksonAnnotations)
  //api(libs.jakarta.persistence.jakartaPersistenceApi)
  api(libs.jakarta.annotation.jakartaAnnotationApi)
  api(libs.jakarta.servlet.jakartaServletApi)
  api(libs.io.swagger.core.v3.swaggerAnnotationsJakarta)
  api(libs.org.slf4j.slf4jApi)

  implementation(libs.org.springframework.security.springSecurityCrypto)

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
