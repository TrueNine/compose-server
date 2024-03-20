project.version = libs.versions.compose.get()

dependencies {
  implementation(libs.jakarta.persistenceApi)
  implementation(libs.kt.kspGoogleApi)
  implementation(libs.util.squareupJavapoet)
  implementation(libs.bundles.kt)
}
