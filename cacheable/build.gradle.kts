version = libs.versions.compose.cacheable.get()

dependencies {
  api(libs.bundles.spring.boot.data.redis)
  implementation(project(":core"))
}
