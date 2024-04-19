version = libs.versions.compose.get()

dependencies {
  api(libs.bundles.spring.redis)
  api(libs.com.github.ben.manes.caffeine.caffeine)
  implementation(project(":core"))
}
