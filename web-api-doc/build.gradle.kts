version = libs.versions.compose.get()

dependencies {
  implementation(project(":core"))
  implementation(libs.org.springdoc.springdoc.openapi.starter.webmvc.ui)
}
