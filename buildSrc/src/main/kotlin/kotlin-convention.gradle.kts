import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val libs = the<LibrariesForLibs>()

plugins {
  id("java-convention")
  id("kotlin-code-format-convention")
  kotlin("jvm")
  kotlin("kapt")
}

dependencies {
  implementation(libs.org.jetbrains.kotlin.kotlin.stdlib)
  implementation(libs.org.jetbrains.kotlin.kotlin.reflect)
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.fromTarget(libs.versions.java.get())
    freeCompilerArgs = listOf(
      "-Xjsr305=strict",
      "-Xjvm-default=all",
      "-verbose"
    )
  }
  jvmToolchain(libs.versions.java.get().toInt())
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
  jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.WARNING)
}

kapt {
  correctErrorTypes = true
  keepJavacAnnotationProcessors = true
}
