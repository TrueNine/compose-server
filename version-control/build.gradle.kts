
plugins {
  kotlin("jvm") version "1.9.0"
  java
  `java-library`
  `java-gradle-plugin`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(gradleApi())
  implementation("${kotlin("stdlib")}:1.9.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    incremental = true
    freeCompilerArgs = listOf(
      "-Xjsr305=strict",
      "-Xjvm-default=all",
      "-verbose",
      "-Xjdk-release=17"
    )
    jvmTarget = "17"
  }
}

gradlePlugin {
  plugins {
    register("net.yan100.compose.plugin") {
      id = "net.yan100.compose.plugin"
      implementationClass = "net.yan100.compose.plugin.Main"
    }
  }
}
