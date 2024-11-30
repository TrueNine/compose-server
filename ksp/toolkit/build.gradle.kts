version = libs.versions.composeKspToolkit.get()

dependencies {
  implementation(libs.com.google.devtools.ksp.symbolProcessingApi)
  implementation(libs.com.squareup.kotlinpoetJvm)
  implementation(libs.com.squareup.kotlinpoetKsp)
  implementation(project(":meta"))
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
