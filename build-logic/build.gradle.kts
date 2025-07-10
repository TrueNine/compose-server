plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

  implementation(libs.com.vanniktech.maven.publish.com.vanniktech.maven.publish.gradle.plugin)
  implementation(libs.io.spring.gradle.dependency.management.plugin)
  implementation(libs.com.diffplug.spotless.com.diffplug.spotless.gradle.plugin)
  implementation(libs.org.jetbrains.kotlin.jvm.org.jetbrains.kotlin.jvm.gradle.plugin)
  implementation(libs.org.jetbrains.kotlin.multiplatform.org.jetbrains.kotlin.multiplatform.gradle.plugin)
  implementation(libs.org.jetbrains.kotlin.kapt.org.jetbrains.kotlin.kapt.gradle.plugin)
  implementation(libs.org.jetbrains.kotlin.plugin.spring.org.jetbrains.kotlin.plugin.spring.gradle.plugin)
}
