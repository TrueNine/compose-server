plugins {
  alias(libs.plugins.com.google.devtools.ksp)
  `kotlinspring-convention`
}

version = libs.versions.compose.rds.get()

kapt {
  javacOptions { option("querydsl.entityAccessors", "true") }
  arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
}

ksp { arg("net.yan100.compose.ksp.plugin.generateJpa", "true") }

dependencies {
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
  api(libs.org.springframework.boot.spring.boot.starter.data.jpa)
  implementation(libs.org.hibernate.orm.hibernate.core)

  implementation(libs.org.springframework.security.spring.security.crypto)

  kapt(libs.com.querydsl.querydsl.apt)
  implementation(libs.com.querydsl.querydsl.jpa)

  ksp(projects.ksp.kspPlugin)
  ksp(libs.org.babyfish.jimmer.jimmer.ksp)

  implementation(libs.org.babyfish.jimmer.jimmer.spring.boot.starter)

  implementation(projects.core)
  implementation(projects.meta)
  implementation(projects.rds.rdsCore)

  implementation(libs.com.fasterxml.jackson.core.jackson.databind)

  testImplementation(projects.security.securityCrypto)
  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.flywaydb.flyway.core)
  testImplementation(projects.rds.rdsMigrationH2)
}
