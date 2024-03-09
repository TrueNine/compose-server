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
package net.yan100.compose.plugin.entrance

import javax.inject.Inject
import net.yan100.compose.plugin.clean.CleanExtensionConfig
import net.yan100.compose.plugin.jar.JarExtensionConfig
import net.yan100.compose.plugin.properties.GradlePropertiesGeneratorConfig
import net.yan100.compose.plugin.publish.PublishExtensionConfig
import net.yan100.compose.plugin.readme.ReadmeEnvRequirementFillerConfig
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.JvmEcosystemPlugin
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.apply

abstract class ConfigEntrance(@Inject val project: Project) : ExtensionAware {
  val cleanExtension = CleanExtensionConfig()
  val publishExtension = PublishExtensionConfig()
  val gradlePropertiesGenerator = GradlePropertiesGeneratorConfig()
  val readmeEnvRequirementFiller = ReadmeEnvRequirementFillerConfig(project)
  val jarExtension = JarExtensionConfig(project)

  private inner class GradleDelegator(project: Project) : Project by project

  internal val isRootProject: Boolean
    get() {
      val notParent = null == project.parent
      val isRootProject = project == project.rootProject
      return notParent && isRootProject
    }

  /**
   * ## jar 打包扩展配置
   *
   * @param action 打包配置
   */
  fun jarExtension(action: Action<JarExtensionConfig>) {
    action.execute(this.jarExtension)
  }

  /**
   * ## 读取环境变量扩展配置
   *
   * @param action 读取配置
   */
  fun readmeEnvRequirementFiller(action: Action<ReadmeEnvRequirementFillerConfig>) {
    action.execute(readmeEnvRequirementFiller)
  }

  /**
   * ## maven or 其他仓库发布扩展配置
   *
   * @param action 发布配置
   */
  fun publishExtension(action: Action<PublishExtensionConfig>) = action.execute(publishExtension)

  /**
   * ## gradle properties 生成扩展配置
   *
   * @param action 生成配置
   */
  fun gradlePropertiesGenerator(action: Action<GradlePropertiesGeneratorConfig>) =
    action.execute(gradlePropertiesGenerator)

  /**
   * ## gradle clean 扩展配置
   *
   * @param action 清除配置
   */
  fun cleanExtension(action: Action<CleanExtensionConfig>) = action.execute(cleanExtension)

  /* === dsl === */

  val languages: SetProperty<String>
  val sourceSet: Property<SourceSet>
  val logger: org.gradle.api.logging.Logger
  val jvm = project.plugins.apply(JvmEcosystemPlugin::class)

  init {
    logger = project.logger
    languages = project.objects.setProperty(String::class.java).convention(listOf("java", "kotlin"))
    sourceSet = project.objects.property(SourceSet::class.java).convention(mainSourceSet(project))
  }

  private fun mainSourceSet(project: Project): SourceSet {
    return resolveSourceSet(SourceSet.MAIN_SOURCE_SET_NAME, project)
  }

  private fun resolveSourceSet(name: String, project: Project): SourceSet {
    val javaPluginExtension = project.extensions.getByType(JavaPluginExtension::class.java)
    return javaPluginExtension.sourceSets.getByName(name)
  }

  companion object {
    const val DSL_NAME = "composeGradle"
  }
}
