version = libs.versions.compose.dependHttpExchange.get()

dependencies {
  api(libs.org.springframework.boot.springBootStarterWebflux)
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
