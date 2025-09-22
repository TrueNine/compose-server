pluginManagement {
  includeBuild("build-logic")
  repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.spring.io/milestone")
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
  // See https://central.sonatype.com/artifact/io.gitee.zjarlin.auto-modules/io.gitee.zjarlin.auto-modules.gradle.plugin
  id("io.gitee.zjarlin.auto-modules") version "0.0.608"
}

autoModules { excludeModules("build-logic") }

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "compose-server"
