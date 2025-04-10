plugins {
  // https://github.com/littlerobots/version-catalog-update-plugin
  alias(libs.plugins.nl.littlerobots.version.catalog.update)
  idea
  base
}

idea {
  module {
    isDownloadSources = true
    isDownloadJavadoc = true
  }
}

versionCatalogUpdate {
  versionSelector(object : nl.littlerobots.vcu.plugin.resolver.ModuleVersionSelector {
    override fun select(candidate: nl.littlerobots.vcu.plugin.resolver.ModuleVersionCandidate): Boolean {
      val g = candidate.candidate.group
      val v = candidate.candidate.version
      return when {
        g == libs.versions.group.get() -> false
        v.lowercase().contains("snapshot") -> false
        v.lowercase().contains("alpha") -> false
        v.lowercase().contains("beta") -> false
        else -> true
      }
    }
  })
  keep {
    keepUnusedVersions = true
  }
}

tasks.register("cleanAll") {
  group = "build"
  description = "清理所有项目（包括子项目）的构建产物，包括 build、.kotlin、bin 和 .logs 目录"

  doLast {
    allprojects {
      project.delete(
        fileTree(project.projectDir) {
          include(
            ".kotlin", "bin", ".logs", "build"
          )
        })
    }
  }
}
