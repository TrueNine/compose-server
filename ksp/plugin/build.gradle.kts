project.version = libs.versions.compose.kspPlugin.get()

dependencies {
  implementation(libs.com.google.devtools.ksp.symbolProcessingApi)
  implementation(libs.com.squareup.javapoet)
  implementation(libs.com.squareup.kotlinpoetJvm)
  implementation(libs.com.squareup.kotlinpoetKsp)
  implementation(libs.org.jetbrains.kotlinx.kotlinxIoCore)
  implementation(libs.org.jetbrains.kotlinx.kotlinxIoCoreJvm)
  implementation(project(":ksp:ksp-core"))
  implementation(project(":ksp:ksp-toolkit"))
  implementation(project(":core"))

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