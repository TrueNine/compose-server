plugins {
  alias(libs.plugins.com.google.devtools.ksp)
}

version = libs.versions.composeRdsCrud.get()

kapt {
  correctErrorTypes = true
  keepJavacAnnotationProcessors = true
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

  ksp(project(":ksp:ksp-plugin"))
  ksp(libs.org.babyfish.jimmer.jimmerKsp)

  implementation(libs.org.babyfish.jimmer.jimmerSpringBootStarter)

  implementation(project(":core"))
  implementation(project(":meta"))
  implementation(project(":rds:rds-core"))

  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)

  testImplementation(project(":security:security-crypto"))
  testImplementation(project(":test-toolkit"))
  testImplementation(libs.org.flywaydb.flywayCore)
  testImplementation(project(":rds:rds-migration-h2"))
  //testRuntimeOnly(libs.bundles.p6spy)
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()
      from(components["java"])
    }
  }
}

signing {
  useGpgCmd()
  sign(publishing.publications["maven"])
}
