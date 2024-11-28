version = libs.versions.composeKspCore.get()

dependencies {
  compileOnly(libs.org.springframework.data.springDataCommons)
  compileOnly(libs.org.hibernate.orm.hibernateCore)
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
