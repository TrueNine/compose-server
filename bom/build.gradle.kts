import kotlin.jvm.optionals.getOrNull

plugins {
  `java-platform`
  `publish-convention`
}

version = libs.versions.compose.asProvider().get()

dependencies {
  constraints {
    val allVersionCatalogs = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
    allVersionCatalogs.libraryAliases.forEach { aliasLib ->
      val dependency = allVersionCatalogs.findLibrary(aliasLib).getOrNull()?.get()
      dependency?.also { dep ->
        api(dep)
      }
    }
  }
}
