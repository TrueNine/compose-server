version = libs.versions.compose.depend.webservlet.get()

dependencies {
  api(libs.spring.boot.web)
  api(libs.jakarta.servlet.jakarta.servlet.api)
  implementation(libs.spring.boot.websocket)
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
