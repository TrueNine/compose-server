import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
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

fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("release", "final", "ga", "-jre").any { version.lowercase().contains(it) }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(version)
  return isStable.not()
}

// https://github.com/ben-manes/gradle-versions-plugin
tasks.withType<DependencyUpdatesTask> {
  this.rejectVersionIf {
    isNonStable(candidate.version)
  }
}

catalog { versionCatalog { from(files("../gradle/libs.versions.toml")) } }
