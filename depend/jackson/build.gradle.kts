version = libs.versions.compose.dependJackson.get()

dependencies {
  api(libs.com.fasterxml.jackson.core.jacksonDatabind)
  api(libs.com.fasterxml.jackson.module.jacksonModuleKotlin)

  implementation(libs.org.springframework.springWeb)
  implementation(libs.com.fasterxml.jackson.datatype.jacksonDatatypeJsr310)
  implementation(libs.com.fasterxml.jackson.datatype.jacksonDatatypeJdk8)
  implementation(libs.com.fasterxml.jackson.datatype.jacksonDatatypeGuava)
  implementation(libs.com.fasterxml.jackson.datatype.jacksonDatatypeJoda)

  implementation(project(":core"))

  testImplementation(project(":test-toolkit"))
  testImplementation(libs.org.springframework.boot.springBootStarterJson)
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
