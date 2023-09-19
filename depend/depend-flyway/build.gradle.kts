import net.yan100.compose.plugin.V

project.version = libs.versions.compose.depend.flyway.get()

dependencies {
  api("org.flywaydb:flyway-core")
  api("org.flywaydb:flyway-mysql")
  implementation(project(":core"))
}
