version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(libs.bundles.knife4j)
  implementation(project(":core"))
}
