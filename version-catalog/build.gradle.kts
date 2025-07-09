import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import kotlin.jvm.optionals.getOrNull

plugins {
  java
  id("buildlogic.repositories-conventions")
  alias(libs.plugins.com.github.ben.manes.versions)
  `version-catalog`
  id("buildlogic.publish-conventions")
}

repositories {
  mavenCentral()
}

java {
  val jv = JavaVersion.VERSION_17
  sourceCompatibility = jv
  targetCompatibility = jv
  toolchain { languageVersion.set(JavaLanguageVersion.of(jv.ordinal + 1)) }
  withSourcesJar()
}

configurations.all {
  resolutionStrategy {
    // 解决kotlin-test-framework-impl冲突
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-testng")
  }
}

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

val skipGroups = listOf(
  "org.jetbrains.kotlin",
  "org.springframework",
  "com.google.devtools"
)

fun isNonStable(version: ModuleComponentIdentifier): Boolean {
  if (skipGroups.any { version.group.startsWith(it) }) {
    return false
  }
  val nonStableKeywords = listOf("alpha", "beta", "rc", "cr", "m", "eap", "dev", "snapshot")
  return nonStableKeywords.any { version.version.contains(it, true) }
}
// https://github.com/ben-manes/gradle-versions-plugin
/*tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    isNonStable(candidate)
  }
}*/

// https://github.com/ben-manes/gradle-versions-plugin
catalog { versionCatalog { from(files("../gradle/libs.versions.toml")) } }
