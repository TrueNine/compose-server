version = libs.versions.compose.get()

dependencies {
  api(libs.spring.integration.mqtt)
  implementation(project(":core"))
}
