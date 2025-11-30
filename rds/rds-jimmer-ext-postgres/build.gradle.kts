plugins {
  id("buildlogic.kotlin-spring-boot-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  PostgreSQL-specific extensions and enhancements for Jimmer ORM framework.
  Provides PostgreSQL-optimized features, custom types, and database-specific functionality.
  """
    .trimIndent()

dependencies {
  implementation(libs.org.babyfish.jimmer.jimmer.sql.kotlin)

  testImplementation(projects.testtoolkit.testtoolkitTestcontainers)
}
