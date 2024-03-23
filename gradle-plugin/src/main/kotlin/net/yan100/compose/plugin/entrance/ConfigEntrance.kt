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
package net.yan100.compose.plugin.entrance

import javax.inject.Inject
import net.yan100.compose.plugin.clean.CleanExtensionConfig
import net.yan100.compose.plugin.filler.FillerConfig
import net.yan100.compose.plugin.ide.IdeExtensionConfig
import net.yan100.compose.plugin.jar.JarExtensionConfig
import net.yan100.compose.plugin.properties.GradlePropertiesGeneratorConfig
import net.yan100.compose.plugin.publish.PublishExtensionConfig
import net.yan100.compose.plugin.spotless.SpotlessConfig
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
  val filler = FillerConfig(project)
  val jarExtension = JarExtensionConfig(project)
  val ideExtension = IdeExtensionConfig()
  val spotless = SpotlessConfig()

  /** ## spotless 扩展配置 */
  fun spotless(action: Action<SpotlessConfig>) = action.execute(spotless)

  /**
   * ## ide 扩展配置
   *
   * @param action ide 配置
   */
  fun ideExtension(action: Action<IdeExtensionConfig>) = action.execute(ideExtension)

  /**
   * ## jar 打包扩展配置
   *
   * @param action 打包配置
   */
  fun jarExtension(action: Action<JarExtensionConfig>) = action.execute(jarExtension)

  /**
   * ## 配置文件填充器
   *
   * @param action 配置
   */
  fun filler(action: Action<FillerConfig>) = action.execute(filler)

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
  fun gradlePropertiesGenerator(action: Action<GradlePropertiesGeneratorConfig>) = action.execute(gradlePropertiesGenerator)

  /**
   * ## gradle clean 扩展配置
   *
   * @param action 清除配置
   */
  fun cleanExtension(action: Action<CleanExtensionConfig>) = action.execute(cleanExtension)

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
