import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()
val targetJavaVersion = libs.versions.java.get().toInt()
val maxSupportedKotlinJvmTarget = JvmTarget.entries.maxOf { it.target.toIntOrNull() ?: 0 }
val kotlinJvmTarget = minOf(targetJavaVersion, maxSupportedKotlinJvmTarget).toString()

plugins {
  id("buildlogic.java-conventions")
  kotlin("jvm")
}

dependencies {
  implementation(libs.org.jetbrains.kotlin.kotlin.stdlib)
  implementation(libs.org.jetbrains.kotlin.kotlin.reflect)
}

kotlin {
  compilerOptions {
    // Keep Kotlin on the highest bytecode level it currently supports while the build JVM/toolchain moves ahead.
    jvmTarget = JvmTarget.fromTarget(kotlinJvmTarget)
    freeCompilerArgs = listOf(
      "-Xjsr305=strict", "-Xjvm-default=all"
    )
  }
  jvmToolchain(targetJavaVersion)
}

// Ensure the kotlin_module file is generated correctly
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.WARNING)
  compilerOptions {
    // Ensure the module metadata is generated correctly
    javaParameters.set(true)
  }
}


// Configure the jar task to include the LICENSE file
tasks.withType<Jar> {
  from(rootProject.file("LICENSE")) {
    into("META-INF")
  }
}
