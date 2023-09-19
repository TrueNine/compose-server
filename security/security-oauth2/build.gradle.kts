import net.yan100.compose.plugin.V

project.version = libs.versions.compose.secuirty.oauth2.get()

dependencies {
  implementation(project(":core"))
  implementation(project(":depend:depend-web-client"))
  implementation("org.springframework.security:spring-security-core")
}
