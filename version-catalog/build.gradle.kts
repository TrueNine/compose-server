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
        compileOnly(d)
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
)

val ignoreGroups = listOf(
  "dev.langchain4j",
  "io.projectreactor.kotlin"
)

fun isNonStable(version: ModuleComponentIdentifier): Boolean {
  return nonStableKeywords.any { version.version.contains(it, true) }
}

// https://github.com/ben-manes/gradle-versions-plugin
tasks.withType<DependencyUpdatesTask> {
  // 拒绝不稳定版本
  rejectVersionIf {
    if (ignoreGroups.any { group?.contains(it, true) == true }) {
      return@rejectVersionIf true
    }
    isNonStable(candidate)
  }

  // 检查构建脚本依赖
  checkBuildEnvironmentConstraints = true

  // 输出格式配置
  outputFormatter = "json,xml,html,plain"

  // 输出目录
  outputDir = "build/dependencyUpdates"

  // 报告文件名
  reportfileName = "report"
}

description =
  """
Version catalog module for managing and publishing dependency versions across the project ecosystem.
Provides centralized version management and dependency update capabilities with automated version checking.
"""
    .trimIndent()

// https://github.com/ben-manes/gradle-versions-plugin
catalog { versionCatalog { from(files("../gradle/libs.versions.toml")) } }
