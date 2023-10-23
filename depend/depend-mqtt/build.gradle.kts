version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.spring.integration.mqtt)
  implementation(project(":core"))
}
