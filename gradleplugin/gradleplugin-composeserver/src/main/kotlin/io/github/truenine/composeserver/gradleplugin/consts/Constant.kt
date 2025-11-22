package io.github.truenine.composeserver.gradleplugin.consts

object Constant {
  const val TASK_GROUP = "compose gradle"

  object Internal {
    const val META_INIT_GRADLE_KTS = "meta.init.gradle.kts"
    const val INIT_GRADLE_KTS = "init.gradle.kts"
  }

  object FileName {
    const val LICENSE = "LICENSE"
  }

  object FileNameSet {
    val LICENSE = setOf("license", "license.txt", FileName.LICENSE).map { it.lowercase() }
  }

  object Gradle {
    const val UNKNOWN_PROJECT_VERSION = "unspecified"
  }
}
