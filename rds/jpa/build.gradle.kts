plugins {
  alias(libs.plugins.com.google.devtools.ksp)
}

version = libs.versions.composeRdsJpa.get()

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
  implementation(project(":meta"))

  implementation(project(":core"))
  implementation(project(":rds:rds-core"))

  testImplementation(project(":security:security-crypto"))

  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)

  testImplementation(project(":security:security-crypto"))
  testImplementation(project(":test-toolkit"))
  testRuntimeOnly(libs.bundles.p6spy)
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
