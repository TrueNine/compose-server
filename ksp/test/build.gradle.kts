plugins {
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
  alias(libs.plugins.org.jetbrains.kotlin.kapt)
  alias(libs.plugins.com.google.devtools.ksp)
  alias(libs.plugins.org.hibernate.orm)
}
apply(plugin = libs.plugins.org.jetbrains.kotlin.plugin.allopen.get().pluginId)
kapt {
  correctErrorTypes = true
  keepJavacAnnotationProcessors = true
  javacOptions { option("querydsl.entityAccessors", true) }
  arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
}
noArg { annotations("jakarta.persistence.MappedSuperclass", "jakarta.persistence.Entity") }
allOpen { annotations("jakarta.persistence.MappedSuperclass", "jakarta.persistence.Entity") }

hibernate {
  enhancement {
    enableAssociationManagement.set(true)
    enableDirtyTracking.set(true)
    enableLazyInitialization.set(true)
  }
}
dependencies {
  ksp(project(":ksp"))
  implementation(project(":ksp:ksp-core"))
  implementation(project(":rds"))
  implementation(project(":core"))
  implementation(project(":rds:rds-core"))
}
