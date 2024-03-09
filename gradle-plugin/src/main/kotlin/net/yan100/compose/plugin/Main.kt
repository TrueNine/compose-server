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

import net.yan100.compose.plugin.clean.CleanExtension
import net.yan100.compose.plugin.entrance.ConfigEntrance
import net.yan100.compose.plugin.jar.JarExtension
import net.yan100.compose.plugin.properties.GradlePropertiesGenerator
import net.yan100.compose.plugin.publish.PublishExtension
import net.yan100.compose.plugin.readme.ReadmeFiller
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class Main : Plugin<Project> {
  override fun apply(project: Project) {
    val cfg = project.extensions.create<ConfigEntrance>(ConfigEntrance.DSL_NAME, project)

    project.afterEvaluate { p ->
      val log = cfg.logger
      log.debug("enhance start = {}", p.name)

      val clean = CleanExtension(p, cfg.cleanExtension)
      log.debug("注册清理任务 = {}", clean)

      val publish = PublishExtension(p, cfg.publishExtension)
      log.debug("注册发布增强任务 = {}", publish)

      val gradlePropertiesGenerator = GradlePropertiesGenerator(p, cfg.gradlePropertiesGenerator)
      log.debug("注册 properties 生成器 = {}", gradlePropertiesGenerator)

      val readmeFiller = ReadmeFiller(p, cfg.readmeEnvRequirementFiller)
      log.debug("注册 readme 填充器 = {}", readmeFiller)

      var jarExtension = JarExtension(project, cfg.jarExtension)
      log.debug("注册 jar 扩展 = {}", jarExtension)
    }
  }
}
