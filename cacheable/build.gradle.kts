version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.bundles.spring.boot.data.redis)
  implementation(project(":core"))
}
