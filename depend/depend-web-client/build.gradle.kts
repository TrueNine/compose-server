version = libs.versions.compose.get()

dependencies {
  api(libs.spring.boot.webflux)
  implementation(project(":core"))
}
