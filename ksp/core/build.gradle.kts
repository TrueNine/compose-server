project.version = libs.versions.compose.kspCore.get()

dependencies {
  api(libs.org.springframework.data.springDataCommons)
  api(libs.org.hibernate.orm.hibernateCore)
  api(project(":rds:rds-core"))
  api(project(":core"))
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
