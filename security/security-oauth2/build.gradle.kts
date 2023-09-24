project.version = libs.versions.compose.security.oauth2.get()

dependencies {
  implementation(libs.spring.security.core)
  implementation(project(":core"))
  implementation(project(":depend:depend-web-client"))
}
