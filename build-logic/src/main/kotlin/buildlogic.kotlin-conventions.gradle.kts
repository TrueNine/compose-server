import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

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
    jvmTarget = JvmTarget.fromTarget(libs.versions.java.get())
    freeCompilerArgs = listOf(
      "-Xjsr305=strict", "-Xjvm-default=all"
    )
  }
  jvmToolchain(libs.versions.java.get().toInt())
}

// Ensure the kotlin_module file is generated correctly
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
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

