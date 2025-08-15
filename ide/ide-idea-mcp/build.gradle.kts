plugins {
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
  alias(libs.plugins.org.jetbrains.intellij.platform)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
  id("buildlogic.spotless-conventions")
}

description = "IntelliJ IDEA plugin for Compose Server MCP integration"

repositories {
  mavenCentral()
  intellijPlatform { defaultRepositories() }
}

dependencies {
  intellijPlatform {
    intellijIdeaCommunity(libs.versions.intellij.platform.asProvider().get())

    bundledPlugin("org.jetbrains.plugins.terminal")
    bundledPlugin("Git4Idea")

    testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    plugin("com.intellij.mcpServer", "1.0.30")
  }

  // Logging dependencies
  implementation(libs.org.slf4j.slf4j.api)
  implementation(libs.ch.qos.logback.logback.classic)

  testImplementation(libs.org.jetbrains.kotlin.kotlin.test)
  testImplementation(libs.io.mockk.mockk) {
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-bom")
  }
}

dependencies { compileOnly(libs.org.jetbrains.kotlinx.kotlinx.serialization.json) }

java {
  sourceCompatibility = JavaVersion.toVersion(libs.versions.intellij.platform.plugin.java.get().toInt())
  targetCompatibility = JavaVersion.toVersion(libs.versions.intellij.platform.plugin.java.get().toInt())
  toolchain { languageVersion = JavaLanguageVersion.of(libs.versions.intellij.platform.plugin.java.get().toInt()) }
}

kotlin { jvmToolchain(libs.versions.intellij.platform.plugin.java.get().toInt()) }

intellijPlatform {
  pluginConfiguration {
    ideaVersion { sinceBuild = libs.versions.intellij.platform.ide.get() }
    description = file("README.html").readText()
    changeNotes = file("CHANGELOG.html").readText()
  }
}
