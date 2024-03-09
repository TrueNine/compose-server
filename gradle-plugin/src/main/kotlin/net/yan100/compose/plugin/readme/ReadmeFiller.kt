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
package net.yan100.compose.plugin.readme

import java.io.File
import net.yan100.compose.plugin.consts.PluginConsts
import org.gradle.api.Project

class ReadmeFiller(
  private val project: Project,
  private val env: ReadmeEnvRequirementFillerConfig,
) {
  private val isRootProject = project == project.rootProject

  init {
    if (isRootProject) {
      createTask()
    }
  }

  private fun createTask() {
    project.tasks.create(TASK_NAME) { task ->
      task.group = PluginConsts.TASK_GROUP
      var fileExists = false

      var file: File? = null

      task.doFirst {
        val readmeFile =
          project.layout.projectDirectory.asFile.listFiles()?.firstOrNull { file ->
            PluginConsts.README_FILE_NAMES.any { file.name.equals(it, ignoreCase = true) }
          }
        fileExists = null != readmeFile
        file = readmeFile
      }

      task.doLast { _ ->
        if (fileExists) {
          var readmeText = ""
          file
            ?.bufferedReader()
            ?.use { readmeText = it.readText().replace(env.regx, env.replaced()) }
            ?.also { file?.writeText(readmeText) }
        }
      }
    }
  }

  companion object {
    const val TASK_NAME = "readmeFill"
  }
}
