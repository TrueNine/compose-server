import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val libs = the<LibrariesForLibs>()

plugins {
  id("java-convention")
  kotlin("jvm")
  kotlin("kapt")
}

dependencies {
  implementation(libs.org.jetbrains.kotlin.kotlinStdlib)
  implementation(libs.org.jetbrains.kotlin.kotlinReflect)
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.fromTarget(libs.versions.java.get())
    freeCompilerArgs = listOf(
      "-Xjsr305=strict",
      "-Xjvm-default=all",
      "-verbose",
      "-Xjdk-release=${libs.versions.java.get()}",
      "-jvm-target=${libs.versions.java.get()}"
    )
  }
  jvmToolchain(libs.versions.java.get().toInt())
}

kapt {
  correctErrorTypes = true
  keepJavacAnnotationProcessors = true
}
