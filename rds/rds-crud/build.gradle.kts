plugins {
  alias(libs.plugins.com.google.devtools.ksp)
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  CRUD operation abstractions and utilities built on top of Jimmer ORM.
  Provides generic repository patterns, transaction management, and database operation helpers.
  """
    .trimIndent()

dependencies {
  api(libs.org.springframework.boot.spring.boot.persistence)

  ksp(libs.org.babyfish.jimmer.jimmer.ksp)
  implementation(libs.org.babyfish.jimmer.jimmer.spring.boot.starter)

  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)

  implementation(libs.org.springframework.security.spring.security.crypto)

  api(projects.shared)
  api(projects.rds.rdsShared)

  implementation(libs.com.fasterxml.jackson.core.jackson.databind)

  testImplementation(libs.org.springframework.boot.spring.boot.starter.flyway)
  testImplementation(projects.testtoolkit.testtoolkitTestcontainers)
  testImplementation(projects.security.securityCrypto)
  testImplementation(libs.org.flywaydb.flyway.core)
  testImplementation(libs.org.flywaydb.flyway.database.postgresql)
  testImplementation(libs.org.postgresql.postgresql)
}
