version = libs.versions.composeDependSpringdocOpenapi.get()

dependencies {
  implementation(project(":core"))
  implementation(libs.org.springdoc.springdocOpenapiStarterWebmvcUi)
  testImplementation(project(":test-toolkit"))
  testImplementation(libs.org.springframework.boot.springBootTest)
  testImplementation(libs.org.springframework.boot.springBootStarterTest)
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
