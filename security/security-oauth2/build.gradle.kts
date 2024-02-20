project.version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(libs.spring.security.core)
  implementation(project(":core"))
  implementation(project(":depend:depend-web-client"))
  testImplementation(libs.spring.boot.web)
}
