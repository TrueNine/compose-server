package net.yan100.compose.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.wrapper.Wrapper
import java.net.URI

class Main : Plugin<Project> {
  override fun apply(project: Project) {
  }

  companion object {}
}

class SettingsMain : Plugin<Settings> {
  override fun apply(target: Settings) {
    target.pluginManagement {
      it.repositories { ir ->
        ir.chinaRegionRepositories()
        println("开始加载")
        ir.forEach { e ->
          println(e)
        }
      }
    }
  }

  companion object {}


}


fun org.gradle.api.artifacts.dsl.DependencyHandler.allAnnotationCompileOnly(dependencyNotation: Any): org.gradle.api.artifacts.Dependency? {
  this.add("annotationProcessor", dependencyNotation)
  this.add("kapt", dependencyNotation)
  this.add("testCompileOnly", dependencyNotation)
  this.add("testAnnotationProcessor", dependencyNotation)
  return this.add("compileOnly", dependencyNotation)
}

fun org.gradle.api.artifacts.dsl.DependencyHandler.annotationProcessorKapt(dependencyNotation: Any): org.gradle.api.artifacts.Dependency? {
  this.add("annotationProcessor", dependencyNotation)
  return this.add("kapt", dependencyNotation)
}

fun RepositoryHandler.chinaRegionRepositories() {
  Repos.publicRepositories.forEach { url ->
    this.maven {
      it.url = URI(url)
    }
  }
}


fun RepositoryHandler.aliYunXiao(releaseUrl: String = Repos.yunXiaoRelese, snapshotUrl: String? = Repos.yunXiaoSnapshot) {
  fun get(url: String) {
    this.maven {
      it.isAllowInsecureProtocol = true
      it.url = URI(url)
      it.credentials { c ->
        c.username = Repos.Credentials.yunXiaoUsername
        c.password = Repos.Credentials.yunXiaoPassword
      }
    }
  }

  get(releaseUrl)
  snapshotUrl?.let { get(it) }
}

/**
 * 排除指定的 catalog 依赖
 */
fun ModuleDependency.exclude(dep: Provider<MinimalExternalModuleDependency>) {
  this.exclude(
    mutableMapOf(
      "group" to dep.get().module.group,
      "module" to dep.get().module.name
    )
  )
}

fun Wrapper.distribute(version: String = "8.5", url: String = "https://mirrors.cloud.tencent.com/gradle"): Wrapper {
  distributionUrl = "$url/gradle-${version}-all.zip"
  distributionType = Wrapper.DistributionType.ALL
  gradleVersion = version
  return this
}
