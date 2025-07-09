plugins {
  alias(libs.plugins.com.google.devtools.ksp)
  id("buildlogic.kotlinspring-conventions")
}

dependencies {
  ksp(libs.org.babyfish.jimmer.jimmer.ksp)
  implementation(libs.org.babyfish.jimmer.jimmer.spring.boot.starter)

  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)

  implementation(libs.org.springframework.security.spring.security.crypto)

  api(projects.shared)
  api(projects.rds.rdsShared)

  implementation(libs.com.fasterxml.jackson.core.jackson.databind)

  testImplementation(projects.security.securityCrypto)
  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.flywaydb.flyway.core)
  testImplementation(libs.org.flywaydb.flyway.database.postgresql)
  testImplementation(libs.org.postgresql.postgresql)
}
