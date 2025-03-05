plugins {
  `kotlinspring-convention`
  alias(libs.plugins.com.google.devtools.ksp)
}

version = libs.versions.compose.rds.core.get()

kapt {
  javacOptions { option("querydsl.entityAccessors", "true") }
  arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
}

dependencies {
  ksp(libs.org.babyfish.jimmer.jimmer.ksp)

  implementation(libs.org.springframework.boot.spring.boot.autoconfigure)

  implementation(projects.core)
  implementation(projects.meta)

  implementation(libs.org.springframework.security.spring.security.crypto)

  implementation(libs.org.springframework.data.spring.data.commons)
  implementation(libs.org.springframework.data.spring.data.jpa)

  implementation(libs.com.querydsl.querydsl.core)

  implementation(libs.com.querydsl.querydsl.jpa)
  kapt(libs.com.querydsl.querydsl.apt)

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
  implementation(libs.org.springframework.boot.spring.boot.starter.jdbc)

  testImplementation(projects.testtoolkit)
  testImplementation(projects.rds.rdsMigrationH2)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.data.jpa)
  testImplementation(libs.org.flywaydb.flyway.core)
}
