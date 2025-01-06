plugins {
  `version-catalog`
  `publish-convention`
}

version = libs.versions.composeVersionCatalog.get()

catalog { versionCatalog { from(files("../gradle/libs.versions.toml")) } }
