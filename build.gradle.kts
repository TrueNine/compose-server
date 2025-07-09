plugins {
  // https://github.com/littlerobots/version-catalog-update-plugin
  alias(libs.plugins.nl.littlerobots.version.catalog.update)
  idea
  base
  id("buildlogic.spotless-conventions")
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

repositories { mavenCentral() }

idea {
  module {
    isDownloadSources = true
    isDownloadJavadoc = true
  }
}

versionCatalogUpdate {
  versionSelector(
    object : nl.littlerobots.vcu.plugin.resolver.ModuleVersionSelector {
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
    }
  )
  keep { keepUnusedVersions = true }
}
