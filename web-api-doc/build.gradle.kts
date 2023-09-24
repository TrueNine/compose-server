version = libs.versions.compose.webapidoc.get()

dependencies {
  implementation(libs.bundles.knife4j)
  implementation(project(":core"))
}
