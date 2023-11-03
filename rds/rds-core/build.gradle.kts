version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(project(":core"))
  implementation(libs.spring.data.springDataCommons)
}
