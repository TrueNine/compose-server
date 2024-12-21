version = libs.versions.composeKspTspoet.get()

dependencies {
  implementation(project(":meta"))
  implementation(project(":core"))
  api(libs.org.freemarker.freemarker)

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
