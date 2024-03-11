version = libs.versions.compose.get()

dependencies {
  api(libs.spring.boot.web)
  api(libs.jakarta.servletApi)
  implementation(libs.spring.boot.websocket)
  implementation(project(":core"))
}
