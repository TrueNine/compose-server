import net.yan100.compose.plugin.V

project.version = V.Compose.dependFlyway

dependencies {
  api("org.flywaydb:flyway-core")
  api("org.flywaydb:flyway-mysql")
  implementation(project(":core"))
}
