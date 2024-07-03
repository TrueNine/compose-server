version = libs.versions.compose.test.toolkit.get()

dependencies {
  implementation(project(":core"))
  implementation(libs.org.springframework.springTest)
  implementation(libs.org.springframework.boot.springBootTestAutoconfigure)
  implementation(libs.org.springframework.boot.springBootTest)
  implementation(libs.org.springframework.security.springSecurityTest)
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
