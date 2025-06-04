plugins {
  `kotlinspring-convention`
  alias(libs.plugins.com.google.devtools.ksp)
}

version = libs.versions.compose.rds.get()

dependencies {
  ksp(libs.org.babyfish.jimmer.jimmer.ksp)
  api(projects.shared)
  implementation(projects.meta)

  implementation(libs.org.springframework.security.spring.security.crypto)
  implementation(libs.org.springframework.data.spring.data.commons)
  implementation(libs.org.springframework.data.spring.data.jpa)
  implementation(libs.com.querydsl.querydsl.core)

  implementation(libs.org.hibernate.orm.hibernate.core)

  // jimmer
  implementation(libs.org.babyfish.jimmer.jimmer.core)
  implementation(libs.org.babyfish.jimmer.jimmer.sql)
  implementation(libs.org.babyfish.jimmer.jimmer.spring.boot.starter) {
    exclude(
      group = libs.org.springframework.boot.spring.boot.starter.jdbc.get().module.group,
      module = libs.org.springframework.boot.spring.boot.starter.jdbc.get().module.name,
    )
  }

  testImplementation(libs.org.postgresql.postgresql)
  testImplementation(libs.org.flywaydb.flyway.core)
  testImplementation(libs.org.flywaydb.flyway.database.postgresql)

  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.data.jpa)
  testImplementation(libs.org.flywaydb.flyway.core)
}
