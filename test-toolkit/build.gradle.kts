version = libs.versions.compose.testToolkit.get()

dependencies {
  api(libs.org.slf4j.slf4jApi)

  // spring 测试支持
  api(libs.org.springframework.springTest)
  api(libs.org.springframework.boot.springBootTestAutoconfigure)
  api(libs.org.springframework.boot.springBootTest)
  api(libs.org.springframework.security.springSecurityTest)
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
