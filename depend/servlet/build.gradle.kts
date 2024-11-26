version = libs.versions.composeDependServlet.get()

dependencies {
  api(libs.org.springframework.boot.springBootStarterWeb)
  api(libs.jakarta.servlet.jakartaServletApi)

  implementation(libs.org.springframework.boot.springBootStarterWebsocket)
  implementation(libs.org.springframework.boot.springBootAutoconfigure)

  implementation(project(":core"))
  implementation(project(":security:security-crypto"))
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
