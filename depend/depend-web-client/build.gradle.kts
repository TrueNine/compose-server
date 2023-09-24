version = libs.versions.compose.depend.webclient.get()

dependencies {
  api(libs.spring.boot.webflux)
  implementation(project(":core"))
}
