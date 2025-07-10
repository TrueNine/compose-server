package net.yan100.compose.gradleplugin.consts

object Constant {
  const val TASK_GROUP = "compose gradle"

  object Internal {
    const val META_INIT_GRADLE_KTS = "meta.init.gradle.kts"
    const val INIT_GRADLE_KTS = "init.gradle.kts"
  }

  object Config {
    const val CONFIG_DIR = ".compose-config"
    const val LICENSE_META = "license.meta"
  }

  object FileName {
    const val LICENSE = "LICENSE"
    const val README = "README.md"
  }

  object FileNameSet {
    val README = setOf(FileName.README, "readme.txt", "readme").map { it.lowercase() }
    val LICENSE = setOf("license", "license.txt", FileName.LICENSE).map { it.lowercase() }
  }

  object Gradle {
    const val UNKNOWN_PROJECT_VERSION = "unspecified"
  }

  object PluginId {
    const val MAVEN_PUBLISH = "maven-publish"
  }
}
