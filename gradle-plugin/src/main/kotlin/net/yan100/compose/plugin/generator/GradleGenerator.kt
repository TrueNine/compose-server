/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
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
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.plugin.generator

import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import javax.inject.Inject
import net.yan100.compose.plugin.consts.Constant
import net.yan100.compose.plugin.consts.MavenRepl
import net.yan100.compose.plugin.consts.Repos
import net.yan100.compose.plugin.wrap
import org.gradle.api.Project

/*
 * org.gradle.daemon=true
 * org.gradle.parallel=true
 * org.gradle.caching=false
 * org.gradle.jvmargs=-Xmx8192m -Xms4096m
 * org.gradle.workers.max=64
 */
class GradleGenerator(
  @Inject private val project: Project,
  @Inject private val dsl: GradleGeneratorConfig,
) {

  init {
    project.wrap {
      val propertiesFile = rootProject.layout.projectDirectory.file(GradleGeneratorConfig.GRADLE_PROPERTIES_NAME).asFile
      tasks.create(GENERATE_PROPERTIES_TASK_NAME) {
        it.group = Constant.TASK_GROUP

        it.doLast {
          if (propertiesFile.exists()) {
            propertiesFile.writeText(dsl.toPropertiesString())
          } else {
            propertiesFile.createNewFile()
            propertiesFile.writeText(dsl.toPropertiesString())
          }
        }
      }
      val task =
        tasks.create(GENERATE_INIT_GRADLE_KTS_TASK_NAME) {
          it.group = Constant.TASK_GROUP
          var executed = false
          it.doLast {
            if (!executed && isRootProject) {
              generateInitGradle(gradle.gradleUserHomeDir, dsl.initGradle)
              executed = true
            }
          }
        }
      tasks.findByName("init")?.dependsOn(task)
    }
  }

  private fun generateInitGradle(userHome: File, cfg: GradleGeneratorConfig.InitGradleConfig) {

    val initFile = userHome.listFiles()?.find { it.name.startsWith("init.gradle") }
    initFile?.delete()

    val initGradleKts = userHome.resolve(Constant.Internal.INIT_GRADLE_KTS)
    if (initGradleKts.createNewFile()) {
      val metaStream = this::class.java.classLoader.getResourceAsStream(Constant.Internal.META_INIT_GRADLE_KTS)
      metaStream?.use { byteStream ->
        InputStreamReader(byteStream).use { reader ->
          val template = reader.readText()
          val urls = mutableListOf<String>()
          cfg.mavenType.apply {
            mavenCentralUrl?.also { urls += "${MavenRepl.MAVEN_CENTRAL}__$it" }
            jCenterUrl?.also { urls += "${MavenRepl.JCENTER}__$it" }
            googlePluginUrl?.also { urls += "${MavenRepl.GOOGLE}__$it" }
            gradlePluginUrl?.also { urls += "${MavenRepl.GRADLE_PLUGIN_PORTAL}__$it" }
          }
          if (cfg.enableSpring) {
            cfg.otherRepositories += Repos.springMilestone
            cfg.otherRepositories += Repos.springSnapshot
          }
          if (cfg.enableMybatisPlus) cfg.otherRepositories += Repos.mybatisPlusSnapshot

          val result =
            template
              .replace("-$-", urls.joinToString(",")) // 替换url
              .replace("$-$", cfg.otherRepositories.joinToString(",")) // 替换仓库
          initFile?.let { f ->
            FileWriter(f).use { writer ->
              writer.write(result)
              writer.flush()
            }
          }
        }
      }
    }
  }

  companion object {
    const val GENERATE_PROPERTIES_TASK_NAME = "composeGenerateGradleProperties"
    const val GENERATE_INIT_GRADLE_KTS_TASK_NAME = "composeGenerateInitGradleKts"
  }
}
