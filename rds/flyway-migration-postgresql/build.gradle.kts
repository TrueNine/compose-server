plugins {
  alias(libs.plugins.com.google.devtools.ksp)
  `kotlinspring-convention`
}

version = libs.versions.compose.rds.get()

dependencies {
  runtimeOnly(libs.org.flywaydb.flyway.core)
  runtimeOnly(libs.org.flywaydb.flyway.database.postgresql)

  testRuntimeOnly(libs.com.github.gavlyukovskiy.p6spy.spring.boot.starter)
  testImplementation(libs.org.springframework.spring.jdbc)
  testRuntimeOnly(libs.org.postgresql.postgresql)
  testImplementation(projects.testtoolkit)
}

tasks.withType<Test> { useJUnitPlatform() }
