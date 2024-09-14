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
package net.yan100.compose.gradleplugin

import net.yan100.compose.gradleplugin.entrance.ConfigEntrance
import net.yan100.compose.gradleplugin.generator.GradleGenerator
import net.yan100.compose.gradleplugin.jar.JarExtension
import net.yan100.compose.gradleplugin.spotless.Spotless
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class Main : Plugin<Project> {
  override fun apply(project: Project) {
    val cfg = project.extensions.create<ConfigEntrance>(ConfigEntrance.DSL_NAME, project)

    if (cfg.gradleGenerator.enabled) {
      val gradleGenerator = GradleGenerator(project, cfg.gradleGenerator)
    }

    if (cfg.spotless.enabled) {
      val spotless = Spotless(project, cfg.spotless)
    }

    project.afterEvaluate { s ->
      if (cfg.jarExtension.enabled) {
        s.wrap {
          val jarExtension = JarExtension(this, cfg.jarExtension)
        }
      }
    }
  }
}
