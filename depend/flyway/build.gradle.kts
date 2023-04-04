project.version = V.Project.flyway

dependencies {
  api("org.flywaydb:flyway-core")
  api("org.flywaydb:flyway-mysql")
  implementation(project(":core"))
}
