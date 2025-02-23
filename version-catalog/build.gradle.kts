plugins {
  `version-catalog`
  `publish-convention`
}

version = libs.versions.compose.asProvider().get()

catalog { versionCatalog { from(files("../gradle/libs.versions.toml")) } }
