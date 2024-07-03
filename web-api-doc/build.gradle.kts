version = libs.versions.compose.webapidoc.get()

dependencies {
  implementation(project(":core"))
  implementation(libs.org.springdoc.springdocOpenapiStarterWebmvcUi)
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
