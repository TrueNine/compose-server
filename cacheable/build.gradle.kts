version = libs.versions.compose.get()

dependencies {
  api(libs.bundles.spring.redis)
  api(libs.cache.caffeine)
  implementation(project(":core"))
}
