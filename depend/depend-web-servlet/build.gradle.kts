version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.spring.boot.web)
  api(libs.jakarta.servletApi)
  implementation(libs.spring.boot.websocket)
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
