plugins {
  alias(libs.plugins.com.google.devtools.ksp)

  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
  id("buildlogic.spotless-sql-conventions")
}

// 排除 SQL 文件的 Spotless 格式化，因为 MySQL DELIMITER 语法不兼容
spotless { sql { targetExclude("**/db/migration/**/*.sql") } }

description =
  """
Flyway database migration scripts and configuration for MySQL 8 databases.
Contains versioned SQL migration files and Spring Boot integration for automated database schema evolution.
"""
    .trimIndent()

dependencies {
  implementation(projects.rds.rdsFlywayMigrationShared)

  runtimeOnly(libs.org.flywaydb.flyway.core)
  runtimeOnly(libs.org.flywaydb.flyway.mysql)

  testImplementation(libs.org.flywaydb.flyway.core)
  testRuntimeOnly(libs.org.flywaydb.flyway.mysql)
  testRuntimeOnly(libs.com.github.gavlyukovskiy.p6spy.spring.boot.starter)
  testImplementation(libs.org.springframework.spring.jdbc)
  testRuntimeOnly(libs.com.mysql.mysql.connector.j)
  testImplementation(projects.testtoolkit)
}
