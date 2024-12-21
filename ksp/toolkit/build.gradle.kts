version = libs.versions.composeKspToolkit.get()

dependencies {
  api(libs.com.google.devtools.ksp.symbolProcessingApi)
  api(libs.com.squareup.kotlinpoetJvm)
  api(libs.com.squareup.kotlinpoetKsp)
  api(project(":meta"))
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
