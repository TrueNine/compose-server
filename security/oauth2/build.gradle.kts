project.version = libs.versions.composeSecurityOauth2.get()

dependencies {
  implementation(libs.org.springframework.boot.springBootAutoconfigure)
  kapt(libs.org.springframework.springBootConfigurationProcessor)

  implementation(libs.org.jetbrains.kotlinx.kotlinxCoroutinesCore)

  implementation(project(":core"))
  implementation(project(":depend:depend-http-exchange"))
  implementation(libs.org.springframework.boot.springBootStarterWebflux)

  implementation(project(":security:security-crypto"))
  implementation(libs.org.springframework.security.springSecurityCore)

  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
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
