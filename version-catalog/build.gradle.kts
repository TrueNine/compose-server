import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import kotlin.jvm.optionals.getOrNull

plugins {
  id("buildlogic.repositories-conventions")
  alias(libs.plugins.com.github.ben.manes.versions)
  `version-catalog`
  java
  id("buildlogic.publish-conventions")
}

repositories { mavenCentral() }

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
      if (d.module.group.contains(libs.versions.group.get()) == false) {
        // 排除 BOM 类型的依赖，因为它们应该作为平台导入而不是库依赖
        if (!d.module.name.contains("bom") && !d.module.name.contains("dependencies")) {
          compileOnly(d)
        }
      }
      if (d.module.group.contains("cn.enaium") && d.module.name.contains("immutable-dependency")) {
        return@also
      }
    }
  }
}

val nonStableKeywords = listOf(
  "alpha",
  "beta",
  "dev",
  "-rc",
  "snapshot"
) + (0 until 30).map { "m$it" }

val ignoreGroups = listOf(
  "dev.langchain4j",
  "io.projectreactor.kotlin"
)

fun isNonStable(version: ModuleComponentIdentifier): Boolean {
  return nonStableKeywords.any { version.version.contains(it, true) }
}

// https://github.com/ben-manes/gradle-versions-plugin
tasks.withType<DependencyUpdatesTask>().configureEach {
  notCompatibleWithConfigurationCache("Gradle Versions dependencyUpdates task interacts with project state during execution.")
  // 拒绝不稳定版本
  rejectVersionIf {
    if (ignoreGroups.any { group?.contains(it, true) == true }) {
      return@rejectVersionIf true
    }
    isNonStable(candidate)
  }
}

description =
  """
Version catalog module for managing and publishing dependency versions across the project ecosystem.
Provides centralized version management and dependency update capabilities with automated version checking.
"""
    .trimIndent()

// https://github.com/ben-manes/gradle-versions-plugin
catalog { versionCatalog { from(files("../gradle/libs.versions.toml")) } }
