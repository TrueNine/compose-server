project.version = libs.versions.compose.securityOauth2.get()

dependencies {
  implementation(project(":core"))
  implementation(project(":depend:depend-http-exchange"))
  implementation(project(":security:security-crypto"))
  implementation(libs.org.springframework.security.springSecurityCore)

  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
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
