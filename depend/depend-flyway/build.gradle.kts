version = libs.versions.compose.depend.flyway.get()

dependencies {
  api(libs.bundles.flyway)
  implementation(project(":core"))
}
