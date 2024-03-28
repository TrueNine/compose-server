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
package net.yan100.compose.plugin.ide

import net.yan100.compose.plugin.consts.Constant
import net.yan100.compose.plugin.wrap
import org.gradle.api.Project

class IdeExtension(
  private val project: Project,
  private val dsl: IdeExtensionConfig,
) {
  private val hasIdea = project.plugins.hasPlugin("org.gradle.idea")
  private val hasEclipse = project.plugins.hasPlugin("org.gradle.eclipse")
  private val hasEclipseWtp = project.plugins.hasPlugin("org.gradle.eclipse-wtp")
  private val hasXcode = project.plugins.hasPlugin("org.gradle.xcode")
  private val hasVisualStudio = project.plugins.hasPlugin("org.gradle.visual-studio")

  init {
    project.wrap {
      tasks.register(GEN_TASK_NAME) { task ->
        task.group = Constant.TASK_GROUP
        if (hasIdea) task.dependsOn(tasks.findByName("idea"))
        if (hasEclipse) task.dependsOn(tasks.findByName("eclipse"))
        if (hasEclipseWtp) task.dependsOn(tasks.findByName("eclipseWtp"))
        if (hasXcode) task.dependsOn(tasks.findByName("xcode"))
        if (hasVisualStudio) task.dependsOn(tasks.findByName("visualStudio"))
      }
      tasks.register(CLEAN_TASK_NAME) { task ->
        task.group = Constant.TASK_GROUP
        if (hasIdea) task.dependsOn(tasks.findByName("cleanIdea"))
        if (hasEclipse) task.dependsOn(tasks.findByName("cleanEclipse"))
        if (hasEclipseWtp) task.dependsOn(tasks.findByName("cleanEclipseWtp"))
        if (hasXcode) task.dependsOn(tasks.findByName("cleanXcode"))
        if (hasVisualStudio) task.dependsOn(tasks.findByName("cleanVisualStudio"))
      }
    }
  }

  companion object {
    const val GEN_TASK_NAME = "composeIdeAll"
    const val CLEAN_TASK_NAME = "composeCleanIdeAll"
  }
}
