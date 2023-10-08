dependencies {
  implementation(project(":core"))
  implementation(project(":web-api-doc"))
  implementation(project(":depend:depend-web-servlet"))
  implementation(project(":depend:depend-web-client"))

  developmentOnly(libs.spring.boot.actuator)
  implementation(libs.spring.integration.mqtt)
}
