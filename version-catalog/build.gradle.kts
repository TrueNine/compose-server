import kotlin.jvm.optionals.getOrNull

plugins {
  java
  `repositories-convention`
  `version-catalog`
  `publish-convention`
}

version = libs.versions.compose.asProvider().get()

dependencies {
  val allVersionCatalogs = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
  allVersionCatalogs.libraryAliases.forEach { aliasLib ->
    val dependency = allVersionCatalogs.findLibrary(aliasLib).getOrNull()?.get()
    dependency?.also { d ->
      if (d.group?.contains("net.yan100.compose") == false) {
        compileOnly(d)
      }
    }
  }
}

catalog { versionCatalog { from(files("../gradle/libs.versions.toml")) } }
