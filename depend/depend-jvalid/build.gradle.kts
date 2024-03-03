version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(libs.spring.boot.validation)
  implementation(project(":core"))
}
