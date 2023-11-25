package net.yan100.compose.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

class Main : Plugin<Project> {
  override fun apply(project: Project) {

  }

  companion object
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
