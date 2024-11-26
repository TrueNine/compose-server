plugins {
  alias(libs.plugins.org.jetbrains.kotlin.kapt)
  alias(libs.plugins.com.google.devtools.ksp)
}

version = libs.versions.composeRdsCore.get()

apply(plugin = libs.plugins.org.jetbrains.kotlin.kapt.get().pluginId)
apply(plugin = libs.plugins.org.jetbrains.kotlin.plugin.allopen.get().pluginId)
apply(plugin = libs.plugins.com.google.devtools.ksp.get().pluginId)

allOpen { annotations("jakarta.persistence.MappedSuperclass", "jakarta.persistence.Entity") }

kapt {
  correctErrorTypes = true
  keepJavacAnnotationProcessors = true
  javacOptions { option("querydsl.entityAccessors", "true") }
  arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
}

dependencies {
  implementation(project(":core"))
  implementation(project(":depend:depend-jsr303-validation"))
  implementation(project(":security:security-crypto"))
  implementation(libs.org.springframework.data.springDataCommons)
  implementation(libs.org.springframework.data.springDataJpa)
  implementation(libs.com.querydsl.querydslCore)

  kapt(variantOf(libs.com.querydsl.querydslApt) { classifier("jakarta") })
  implementation(variantOf(libs.com.querydsl.querydslJpa) { classifier("jakarta") })
  implementation(libs.org.hibernate.orm.hibernateCore)

  testImplementation(project(":test-toolkit"))
  testImplementation(libs.org.springframework.boot.springBootStarterDataJpa)
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
