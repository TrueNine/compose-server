plugins {
  java
  `repositories-convention`
  `version-catalog`
  `publish-convention`
}

version = libs.versions.compose.asProvider().get()

// https://github.com/ben-manes/gradle-versions-plugin
catalog { versionCatalog { from(files("../gradle/libs.versions.toml")) } }
