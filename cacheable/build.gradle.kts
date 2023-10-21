version = libs.versions.compose.get()

dependencies {
  api(libs.bundles.spring.boot.data.redis)
  implementation(project(":core"))
}
