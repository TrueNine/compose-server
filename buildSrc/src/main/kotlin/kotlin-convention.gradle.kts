import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val libs = the<LibrariesForLibs>()

plugins {
  id("java-convention")
  kotlin("jvm")
  kotlin("kapt")
}

dependencies {
  implementation(libs.org.jetbrains.kotlin.kotlin.stdlib)
  implementation(libs.org.jetbrains.kotlin.kotlin.reflect)
}

configurations.all {
  val javaToolchainVersion = extensions.findByType<JavaPluginExtension>()?.toolchain?.languageVersion?.get()?.asInt()?.toString()
  if (javaToolchainVersion == null) {
    return@all
  }
  kotlin {
    compilerOptions {
      jvmTarget = JvmTarget.fromTarget(javaToolchainVersion)
      freeCompilerArgs = listOf(
        "-Xjsr305=strict", "-Xjvm-default=all", "-verbose"
      )
    }
    jvmToolchain(javaToolchainVersion.toInt())
  }
}

kapt {
  correctErrorTypes = true
  keepJavacAnnotationProcessors = true
}
