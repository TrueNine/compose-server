project.version = libs.versions.compose.ksp.asProvider().get()

dependencies {
  implementation(libs.com.google.devtools.ksp.symbol.processing.api)
  implementation(libs.util.squareupJavapoet)
  implementation(libs.com.squareup.kotlinpoet.jvm)
  implementation(libs.com.squareup.kotlinpoet.ksp)
  implementation(libs.org.jetbrains.kotlinx.kotlinx.io.core)
  implementation(project(":ksp:ksp-core"))
  implementation(project(":core"))
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
