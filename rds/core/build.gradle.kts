plugins {
  alias(libs.plugins.org.hibernate.orm)
  alias(libs.plugins.org.jetbrains.kotlin.kapt)
  alias(libs.plugins.com.google.devtools.ksp)
}
apply(plugin = libs.plugins.org.jetbrains.kotlin.kapt.get().pluginId)
apply(plugin = libs.plugins.org.jetbrains.kotlin.plugin.noarg.get().pluginId)
apply(plugin = libs.plugins.org.jetbrains.kotlin.plugin.allopen.get().pluginId)
apply(plugin = libs.plugins.com.google.devtools.ksp.get().pluginId)
apply(plugin = libs.plugins.org.hibernate.orm.get().pluginId)

kapt {
  correctErrorTypes = true
  keepJavacAnnotationProcessors = true
  javacOptions { option("querydsl.entityAccessors", true) }
  arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
}
allOpen { annotations("jakarta.persistence.MappedSuperclass", "jakarta.persistence.Entity") }
hibernate {
  enhancement {
    enableDirtyTracking.set(true)
    enableLazyInitialization.set(true)
    enableAssociationManagement.set(true)
  }
}
version = libs.versions.compose.rdsCore.get()

dependencies {
  implementation(project(":core"))
  implementation(project(":depend:depend-jsr303-validation"))
  implementation(project(":security:security-crypto"))
  implementation(libs.org.springframework.data.springDataCommons)
  implementation(libs.org.springframework.data.springDataJpa)
  implementation(libs.com.querydsl.querydslCore)

  kapt(variantOf(libs.com.querydsl.querydslApt) { classifier("jakarta") })
  implementation(variantOf(libs.com.querydsl.querydslJpa) { classifier("jakarta") })

  testImplementation(project(":test-toolkit"))
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
