plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.publish-conventions")
  id("buildlogic.spotless-conventions")
  alias(libs.plugins.org.jetbrains.intellij.platform)
}

description = "IntelliJ IDEA plugin for Compose Server MCP integration"

repositories {
  mavenCentral()
  intellijPlatform { defaultRepositories() }
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
  intellijPlatform {
    create("IC", "2025.2")
    testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    plugin("io.github.truenine.composeserver.ide.ideamcp", "1.0.30")
  }
}

dependencies { compileOnly(libs.org.jetbrains.kotlinx.kotlinx.serialization.json) }

intellijPlatform {
  pluginConfiguration {
    ideaVersion { sinceBuild = "243" }

    changeNotes =
      """
            Initial version
        """
        .trimIndent()
  }
}
