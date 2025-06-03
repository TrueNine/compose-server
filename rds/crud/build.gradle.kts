plugins {
  alias(libs.plugins.com.google.devtools.ksp)
  `kotlinspring-convention`
}

version = libs.versions.compose.rds.get()

dependencies {
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)

  implementation(libs.org.springframework.security.spring.security.crypto)

  ksp(libs.org.babyfish.jimmer.jimmer.ksp)

  implementation(libs.org.babyfish.jimmer.jimmer.spring.boot.starter)

  api(projects.shared)
  api(projects.rds.rdsShared)
  implementation(projects.meta)

  implementation(libs.com.fasterxml.jackson.core.jackson.databind)

  testImplementation(projects.security.securityCrypto)
  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.flywaydb.flyway.core)
  testImplementation(libs.org.flywaydb.flyway.database.postgresql)
  testImplementation(libs.org.postgresql.postgresql)
}
