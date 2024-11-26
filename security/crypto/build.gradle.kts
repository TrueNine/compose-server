version = libs.versions.composeSecurityCrypto.get()

dependencies {
  implementation(libs.org.springframework.boot.springBootAutoconfigure)

  api(libs.org.springframework.security.springSecurityCrypto)

  implementation(libs.org.bouncycastle.bcprovJdk18on)
  implementation(project(":core"))

  testImplementation(libs.com.fasterxml.jackson.core.jacksonDatabind)
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
