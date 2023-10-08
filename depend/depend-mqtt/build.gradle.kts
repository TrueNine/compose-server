version = libs.versions.compose.depend.mqtt.get()

dependencies {
  api(libs.spring.integration.mqtt)
  implementation(project(":core"))
}
