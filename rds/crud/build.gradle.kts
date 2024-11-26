plugins {
  alias(libs.plugins.org.jetbrains.kotlin.kapt)
  alias(libs.plugins.com.google.devtools.ksp)
}

version = libs.versions.composeRdsCrud.get()

kapt {
  correctErrorTypes = true
  keepJavacAnnotationProcessors = true
  javacOptions { option("querydsl.entityAccessors", "true") }
  arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
}

allOpen { annotations("jakarta.persistence.MappedSuperclass", "jakarta.persistence.Entity") }

dependencies {
  api(libs.org.springframework.boot.springBootStarterDataJpa)

  kapt(variantOf(libs.com.querydsl.querydslApt) { classifier("jakarta") })
  implementation(variantOf(libs.com.querydsl.querydslJpa) { classifier("jakarta") })

  ksp(project(":ksp:ksp-plugin"))
  implementation(project(":core"))
  implementation(project(":ksp:ksp-core"))
  implementation(project(":rds:rds-core"))

  implementation(project(":security:security-crypto"))
  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)
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
