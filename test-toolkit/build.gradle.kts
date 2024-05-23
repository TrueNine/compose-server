version = libs.versions.compose.test.toolkit.get()

dependencies {
  implementation(project(":core"))
  implementation(libs.org.springframework.spring.test)
  implementation(libs.org.springframework.boot.spring.boot.test.autoconfigure)
  implementation(libs.org.springframework.boot.spring.boot.test)
  implementation(libs.org.springframework.security.spring.security.test)
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
