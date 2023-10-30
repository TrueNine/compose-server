version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.bundles.spring.redis)
  implementation(project(":core"))
}
