import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("buildlogic.java-conventions")
  kotlin("jvm")
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
  implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.0")
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_17
    freeCompilerArgs = listOf(
      "-Xjsr305=strict", "-Xjvm-default=all", "-verbose"
    )
  }
  jvmToolchain(17)
}
