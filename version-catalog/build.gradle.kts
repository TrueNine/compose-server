import kotlin.jvm.optionals.getOrNull

plugins {
  java
  alias(libs.plugins.com.github.ben.manes.versions)
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
      if (d.module.group.contains("net.yan100.compose") == false) {
        compileOnly(d)
      }
      if (d.module.group.contains("cn.enaium") && d.module.name.contains("immutable-dependency")) {
        return@also
      }
    }
  }
}


// https://github.com/ben-manes/gradle-versions-plugin
catalog { versionCatalog { from(files("../gradle/libs.versions.toml")) } }
