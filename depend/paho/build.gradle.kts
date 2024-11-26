version = libs.versions.composeDependPaho.get()

dependencies {
  implementation(libs.org.springframework.boot.springBootAutoconfigure)
  kapt(libs.org.springframework.springBootConfigurationProcessor)

  implementation(libs.org.springframework.integration.springIntegrationMqtt)
  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)
  implementation(project(":core"))
  testImplementation(project(":test-toolkit"))
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
