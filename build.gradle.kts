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
  keep {
    keepUnusedVersions = true
  }
}
