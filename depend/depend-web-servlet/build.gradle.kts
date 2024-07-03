version = libs.versions.compose.depend.webservlet.get()

dependencies {
  api(libs.org.springframework.boot.springBootStarterWeb)
  api(libs.jakarta.servlet.jakartaServletApi)
  implementation(libs.org.springframework.boot.springBootStarterWebsocket)
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
