plugins {
  id("buildlogic.kotlin-spring-boot-conventions")
  alias(libs.plugins.com.google.devtools.ksp)
  id("buildlogic.spotless-conventions")
}

description =
  """
  Shared components for relational database operations using Jimmer ORM framework.
  Provides common database configurations, entities, and utilities for database access layers.
  """
    .trimIndent()

dependencies {
  ksp(libs.org.babyfish.jimmer.jimmer.ksp)
  api(projects.shared)

  // jimmer
  implementation(libs.org.babyfish.jimmer.jimmer.core)
  implementation(libs.org.babyfish.jimmer.jimmer.sql.kotlin)
  implementation(libs.org.babyfish.jimmer.jimmer.spring.boot.starter) {
    exclude(
      group = libs.org.springframework.boot.spring.boot.starter.jdbc.asProvider().get().module.group,
      module = libs.org.springframework.boot.spring.boot.starter.jdbc.asProvider().get().module.name,
    )
    exclude(
      group = libs.org.springframework.data.spring.data.commons.get().module.group,
      module = libs.org.springframework.data.spring.data.commons.get().module.name,
    )
  }
  implementation(libs.org.springframework.boot.spring.boot.starter.jdbc)
  implementation(libs.org.springframework.data.spring.data.commons)

  testImplementation(projects.testtoolkit.testtoolkitTestcontainers)
  testImplementation(libs.org.postgresql.postgresql)
  testImplementation(libs.org.flywaydb.flyway.core)
  testImplementation(libs.org.flywaydb.flyway.database.postgresql)
  testImplementation(libs.tools.jackson.core.jackson.databind)
  testImplementation(libs.tools.jackson.module.jackson.module.kotlin)
}
