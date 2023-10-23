version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.bundles.flyway)
  implementation(project(":core"))
}
