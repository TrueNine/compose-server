plugins {
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
  alias(libs.plugins.org.jetbrains.kotlin.kapt)
  alias(libs.plugins.com.google.devtools.ksp)
}

apply(plugin = libs.plugins.org.jetbrains.kotlin.plugin.allopen.get().pluginId)

kapt {
  correctErrorTypes = true
  keepJavacAnnotationProcessors = true
  javacOptions { option("querydsl.entityAccessors", "true") }
  arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
}
allOpen { annotations("jakarta.persistence.MappedSuperclass", "jakarta.persistence.Entity") }

dependencies {
  ksp(project(":ksp"))
  implementation(project(":ksp:ksp-core"))
  implementation(project(":rds"))
  implementation(project(":core"))
  implementation(project(":test-toolkit"))
  implementation(project(":rds:rds-core"))
}
