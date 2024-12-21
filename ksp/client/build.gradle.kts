version = libs.versions.composeKspClient.get()

dependencies {
  compileOnly(libs.com.google.devtools.ksp.symbolProcessingApi)

  implementation(libs.com.squareup.kotlinpoetJvm)
  implementation(libs.com.squareup.kotlinpoetKsp)

  implementation(project(":core"))
  implementation(project(":meta"))
  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)
  implementation(libs.com.fasterxml.jackson.module.jacksonModuleKotlin)

  implementation(project(":ksp:ksp-toolkit"))
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
