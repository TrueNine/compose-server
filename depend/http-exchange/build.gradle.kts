version = libs.versions.composeDependHttpExchange.get()

dependencies {
  implementation(project(":core"))
  implementation(libs.org.springframework.springCore)
  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)
  implementation(libs.org.springframework.springWeb)
  implementation(libs.io.netty.nettyHandler)
  implementation(libs.org.springframework.springWebflux)

  testImplementation(libs.org.springframework.boot.springBootStarterWebflux)
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
