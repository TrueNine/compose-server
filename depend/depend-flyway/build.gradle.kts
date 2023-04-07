project.version = V.Project.dependFlyway

dependencies {
  api("org.flywaydb:flyway-core")
  api("org.flywaydb:flyway-mysql")
  implementation(project(":core"))
}
