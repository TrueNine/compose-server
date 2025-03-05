plugins {
  `kotlin-dsl`
}

val springRepo = "https://repo.spring.io/milestone"

repositories {
  gradlePluginPortal()
  mavenCentral()
  maven(springRepo)
}

dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

  api(libs.io.spring.gradle.dependency.management.plugin)
  api(libs.com.diffplug.spotless.com.diffplug.spotless.gradle.plugin)
  api(libs.org.jetbrains.kotlin.jvm.org.jetbrains.kotlin.jvm.gradle.plugin)
  api(libs.org.jetbrains.kotlin.multiplatform.org.jetbrains.kotlin.multiplatform.gradle.plugin)
  api(libs.org.jetbrains.kotlin.kapt.org.jetbrains.kotlin.kapt.gradle.plugin)
  api(libs.org.jetbrains.kotlin.plugin.spring.org.jetbrains.kotlin.plugin.spring.gradle.plugin)
}
