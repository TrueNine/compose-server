plugins {
  alias(libs.plugins.com.google.devtools.ksp)
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
  id("buildlogic.spotless-sql-conventions")
}

description =
  """
Flyway database migration scripts and configuration for PostgreSQL databases.
Contains versioned SQL migration files and Spring Boot integration for automated database schema evolution.
"""
    .trimIndent()

dependencies {
  runtimeOnly(libs.org.flywaydb.flyway.core)
  runtimeOnly(libs.org.flywaydb.flyway.database.postgresql)

  testRuntimeOnly(libs.com.github.gavlyukovskiy.p6spy.spring.boot.starter)
  testImplementation(libs.org.springframework.spring.jdbc)
  testRuntimeOnly(libs.org.postgresql.postgresql)
  testImplementation(projects.testtoolkit)
}

tasks.withType<Test> { useJUnitPlatform() }
