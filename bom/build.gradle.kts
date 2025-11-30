import kotlin.jvm.optionals.getOrNull

plugins {
  `java-platform`
  id("buildlogic.maven-publish-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Bill of Materials (BOM) for centralized dependency version management across all project modules.
  Provides a platform for consistent dependency versions and simplified dependency declarations.
  """
    .trimIndent()

dependencies {
  constraints {
    val allVersionCatalogs = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
    allVersionCatalogs.libraryAliases.forEach { aliasLib ->
      val dependency = allVersionCatalogs.findLibrary(aliasLib).getOrNull()?.get()
      dependency?.also { dep -> api(dep) }
    }
  }
}
