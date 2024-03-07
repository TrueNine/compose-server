/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.plugin

import java.net.URI
import net.yan100.compose.plugin.clean.CleanExtension
import net.yan100.compose.plugin.versioncontrol.VersionControlConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.create

class Main : Plugin<Project> {
  companion object {
    const val HELLO_TASK_NAME = "versionControl"
  }

  override fun apply(project: Project) {
    val cfg =
      project.extensions.create<VersionControlConfig>(VersionControlConfig.DSL_NAME, project)
    val log = cfg.logger
    log.info("enhance start = {}", project.name)

    val clean = CleanExtension(project, cfg.cleanExtension)
    log.info("注册清理任务 = {}", clean)
    // val publish = PublishExtension(project, cfg.publishExtension)
    // log.info("注册发布增强任务 = {}", publish)

    //    project.gradle.taskGraph.whenReady {
    //      val sourceSet = cfg.sourceSet.get()
    //      val languageNames = cfg.languages.get()
    //      for (langName in languageNames) {
    //        val languageCompileTaskName = sourceSet.getCompileTaskName(langName)
    //        val languageCompileTask = project.tasks.findByName(languageCompileTaskName) ?:
    // continue
    //        languageCompileTask.doLast { task -> log.info("version control") }
    //      }
    //    }

    //    project.tasks.create(HELLO_TASK_NAME) {
    //      it.group = "build setup"
    //      it.doLast { log.info("versionControl initialized") }
    //    }
  }
}

fun org.gradle.api.artifacts.dsl.DependencyHandler.allAnnotationCompileOnly(
  dependencyNotation: Any
): org.gradle.api.artifacts.Dependency? {
  this.add("annotationProcessor", dependencyNotation)
  this.add("kapt", dependencyNotation)
  this.add("testCompileOnly", dependencyNotation)
  this.add("testAnnotationProcessor", dependencyNotation)
  return this.add("compileOnly", dependencyNotation)
}

fun org.gradle.api.artifacts.dsl.DependencyHandler.annotationProcessorKapt(
  dependencyNotation: Any
): org.gradle.api.artifacts.Dependency? {
  this.add("annotationProcessor", dependencyNotation)
  return this.add("kapt", dependencyNotation)
}

fun RepositoryHandler.chinaRegionRepositories() {
  Repos.publicRepositories.forEach { url -> this.maven { it.url = URI(url) } }
}

fun RepositoryHandler.aliYunXiao(
  releaseUrl: String = Repos.yunXiaoRelese,
  snapshotUrl: String? = Repos.yunXiaoSnapshot
) {
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

/** 排除指定的 catalog 依赖 */
fun ModuleDependency.exclude(dep: Provider<MinimalExternalModuleDependency>) {
  this.exclude(mutableMapOf("group" to dep.get().module.group, "module" to dep.get().module.name))
}

fun Wrapper.distribute(
  version: String = "8.5",
  url: String = "https://mirrors.cloud.tencent.com/gradle"
): Wrapper {
  distributionUrl = "$url/gradle-${version}-bin.zip"
  distributionType = Wrapper.DistributionType.ALL
  gradleVersion = version
  return this
}
