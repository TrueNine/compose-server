plugins {
  // https://github.com/littlerobots/version-catalog-update-plugin
  alias(libs.plugins.nl.littlerobots.version.catalog.update)
  // https://github.com/jeremylong/DependencyCheck
  id("org.owasp.dependencycheck") version "11.1.0"
  idea
  base
  id("buildlogic.spotless-conventions")
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

repositories { mavenCentral() }

idea {
  module {
    isDownloadSources = true
    isDownloadJavadoc = true
  }
}

// 依赖安全扫描配置
dependencyCheck {
  // 输出格式
  formats = listOf("HTML", "JSON", "XML")

  // 扫描配置
  scanConfigurations = listOf("runtimeClasspath", "compileClasspath")

  // 跳过测试依赖
  skipTestGroups = true

  // 数据库更新
  autoUpdate = true

  // 分析器配置
  analyzers.apply {
    // 启用实验性分析器
    experimentalEnabled = true
    // 启用已知漏洞分析
    knownExploitedEnabled = true
    // 禁用一些可能误报的分析器
    nodeAuditEnabled = false
    nodeEnabled = false
    pyDistributionEnabled = false
    pyPackageEnabled = false
    rubygemsEnabled = false
    bundleAuditEnabled = false
  }

  // 抑制误报
  suppressionFile = file("config/dependency-check-suppressions.xml").takeIf { it.exists() }?.absolutePath
}

versionCatalogUpdate {
  versionSelector(
    object : nl.littlerobots.vcu.plugin.resolver.ModuleVersionSelector {
      override fun select(candidate: nl.littlerobots.vcu.plugin.resolver.ModuleVersionCandidate): Boolean {
        val g = candidate.candidate.group
        val v = candidate.candidate.version
        return when {
          g == libs.versions.group.get() -> false
          v.lowercase().contains("snapshot") -> false
          v.lowercase().contains("alpha") -> false
          v.lowercase().contains("beta") -> false
          else -> true
        }
      }
    }
  )
  keep { keepUnusedVersions = true }
}
