plugins {
  `kotlinspring-convention`
  alias(libs.plugins.com.google.devtools.ksp)
}

version = libs.versions.composeRdsCore.get()

kapt {
  javacOptions { option("querydsl.entityAccessors", "true") }
  arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
}

dependencies {
  ksp(libs.org.babyfish.jimmer.jimmerKsp)

  implementation(libs.org.springframework.boot.springBootAutoconfigure)

  implementation(projects.core)
  implementation(projects.meta)

  implementation(libs.org.springframework.security.springSecurityCrypto)

  implementation(libs.org.springframework.data.springDataCommons)
  implementation(libs.org.springframework.data.springDataJpa)

  implementation(libs.com.querydsl.querydslCore)

  implementation(variantOf(libs.com.querydsl.querydslJpa) { classifier("jakarta") })
  kapt(variantOf(libs.com.querydsl.querydslApt) { classifier("jakarta") })

  implementation(libs.org.hibernate.orm.hibernateCore)

  // jimmer
  implementation(libs.org.babyfish.jimmer.jimmerCore)
  implementation(libs.org.babyfish.jimmer.jimmerSql)
  implementation(libs.org.babyfish.jimmer.jimmerSpringBootStarter)

  testImplementation(projects.testToolkit)
  testImplementation(projects.rds.rdsMigrationH2)
  testImplementation(libs.org.springframework.boot.springBootStarterDataJpa)
  testImplementation(libs.org.flywaydb.flywayCore)
}
