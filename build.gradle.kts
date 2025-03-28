import nl.littlerobots.vcu.plugin.versionSelector

plugins {
  // https://github.com/littlerobots/version-catalog-update-plugin
  alias(libs.plugins.nl.littlerobots.version.catalog.update)
  idea
}

idea {
  module {
    isDownloadSources = true
    isDownloadJavadoc = true
  }
}

versionCatalogUpdate {
  versionSelector {
    val g = it.candidate.group
    val v = it.candidate.version
    when {
      g == libs.versions.group.get() -> false
      v.lowercase().contains("snapshot") -> false
      v.lowercase().contains("alpha") -> false
      v.lowercase().contains("beta") -> false
      else -> true
    }
  }
  keep {
    keepUnusedVersions = true
  }
}
