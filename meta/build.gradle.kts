version = libs.versions.composeMeta.get()

dependencies {
  implementation(libs.com.fasterxml.jackson.core.jacksonAnnotations)

  testImplementation(project(":core"))
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
