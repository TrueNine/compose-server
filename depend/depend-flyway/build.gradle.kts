version = libs.versions.compose.get()

dependencies {
  api(libs.bundles.flyway)
  implementation(project(":core"))
}
