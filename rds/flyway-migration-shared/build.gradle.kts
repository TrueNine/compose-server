plugins {
  alias(libs.plugins.com.google.devtools.ksp)
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
Shared foundation module for Flyway database migrations, providing common dependencies and configurations for various database types.
Serves as a base dependency for specific database migration modules (such as PostgreSQL, MySQL8), offering Flyway Core runtime support.
"""
    .trimIndent()

dependencies {
  api(libs.org.springframework.boot.spring.boot.autoconfigure)
  api(libs.org.springframework.spring.context)

  runtimeOnly(libs.org.flywaydb.flyway.core)

  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.flywaydb.flyway.core)
  testImplementation(libs.org.flywaydb.flyway.database.postgresql)
  testImplementation(libs.org.springframework.spring.jdbc)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.jdbc)
  testRuntimeOnly(libs.org.postgresql.postgresql)
}
