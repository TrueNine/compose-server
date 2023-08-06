plugins {
  `kotlin-dsl`
  kotlin("jvm") version "1.8.20"

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
  implementation(kotlin("stdlib-jdk8"))
}

tasks.compileKotlin {
  kotlinOptions.jvmTarget = "17"
}
tasks.compileTestKotlin {
  kotlinOptions.jvmTarget = "17"
}


gradlePlugin {
  plugins {
    register("net.yan100.compose.plugin") {
      id = "net.yan100.compose.plugin"
      implementationClass = "net.yan100.compose.plugin.Main"
    }
  }
}
