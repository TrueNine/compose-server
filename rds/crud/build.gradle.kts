plugins {
  alias(libs.plugins.com.google.devtools.ksp)
  `kotlinspring-convention`
}

version = libs.versions.composeRdsCrud.get()

kapt {
  javacOptions { option("querydsl.entityAccessors", "true") }
  arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
}

ksp {
  arg("net.yan100.compose.ksp.plugin.generateJpa", "true")
}

dependencies {
  implementation(libs.org.jetbrains.kotlinx.kotlinxCoroutinesCore)
  api(libs.org.springframework.boot.springBootStarterDataJpa)
  implementation(libs.org.hibernate.orm.hibernateCore)

  implementation(libs.org.springframework.security.springSecurityCrypto)

  kapt(variantOf(libs.com.querydsl.querydslApt) { classifier("jakarta") })
  implementation(variantOf(libs.com.querydsl.querydslJpa) { classifier("jakarta") })

  ksp(projects.ksp.kspPlugin)
  ksp(libs.org.babyfish.jimmer.jimmerKsp)

  implementation(libs.org.babyfish.jimmer.jimmerSpringBootStarter)

  implementation(projects.core)
  implementation(projects.meta)
  implementation(projects.rds.rdsCore)

  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)

  testImplementation(projects.security.securityCrypto)
  testImplementation(projects.testToolkit)
  testImplementation(libs.org.flywaydb.flywayCore)
  testImplementation(projects.rds.rdsMigrationH2)
}
