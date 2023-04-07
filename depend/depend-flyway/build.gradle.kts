project.version = V.Project.dependFlyway

dependencies {
  // https://mvnrepository.com/artifact/org.flywaydb/flyway-core
  api("org.flywaydb:flyway-core:${V.Driver.flyway}")
  // https://mvnrepository.com/artifact/org.flywaydb/flyway-mysql
  api("org.flywaydb:flyway-mysql:${V.Driver.flyway}")
  implementation(project(":core"))
}
