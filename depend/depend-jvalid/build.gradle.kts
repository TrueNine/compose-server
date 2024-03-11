version = libs.versions.compose.get()

dependencies {
  implementation(libs.spring.boot.validation)
  implementation(project(":core"))
}
