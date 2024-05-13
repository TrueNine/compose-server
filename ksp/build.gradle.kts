project.version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(libs.jakarta.persistenceApi)
  implementation(libs.kt.kspGoogleApi)
  implementation(libs.util.squareupJavapoet)
  implementation(libs.com.squareup.kotlinpoet.jvm)
  implementation(libs.com.squareup.kotlinpoet.ksp)
  implementation(libs.jakarta.validationApi)
  implementation(libs.org.springframework.data.spring.data.commons)
  implementation(libs.org.jetbrains.kotlinx.kotlinx.io.core)

  implementation(project(":rds:rds-core"))
  implementation(project(":core"))
  implementation(libs.bundles.kt)
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
