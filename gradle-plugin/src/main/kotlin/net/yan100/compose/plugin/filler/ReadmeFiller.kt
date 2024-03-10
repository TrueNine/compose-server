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
package net.yan100.compose.plugin.filler

import java.io.File
import net.yan100.compose.plugin.consts.Constant
import net.yan100.compose.plugin.wrap
import org.gradle.api.Project

class ReadmeFiller(
  private val project: Project,
  private val dsl: FillerConfig,
) {
  init {
    project.wrap {
      if (isRootProject) {
        fillEnvVersion()
        fillLicense()
      }
    }
  }

  private fun fillLicense() =
    project.wrap {
      tasks.register(LICENSE_TASK_NAME) { task ->
        task.group = Constant.TASK_GROUP
        val destLicenseFile = layout.projectDirectory.file(Constant.FileName.LICENSE).asFile
        val configFile = projectConfig.licenseMetaFile.asFile

        if (configFile.exists()) {
          configFile.readText().trimIndent().let { text ->
            if (text.isNotBlank()) {
              val writeText =
                text.replace(("\\$" + "\\{([^\\}]*)}").toRegex()) { match ->
                  val key = match.groups[0]!!.value.replace("\${", "").replace("}", "")
                  dsl.license[key]
                    ?: throw IllegalArgumentException("license meta scope variable $key not found")
                }
              if (!destLicenseFile.exists()) {
                destLicenseFile.createNewFile()
              }
              val newLine = if (dsl.license.newLine) "\n" else ""
              destLicenseFile.writeText(writeText + newLine)
            }
          }
        } else
          throw IllegalArgumentException(
            "license meta file not found in ${configFile.absolutePath}"
          )
      }
    }

  private fun fillEnvVersion() =
    project.wrap {
      tasks.register(README_TASK_NAME) { task ->
        task.group = Constant.TASK_GROUP
        var fileExists = false
        var file: File? = null

        task.doFirst {
          val readmeFile =
            layout.projectDirectory.asFile.listFiles()?.firstOrNull { file ->
              Constant.FileNameSet.README.any { file.name.equals(it, ignoreCase = true) }
            }
          fileExists = null != readmeFile
          file = readmeFile
        }

        task.doLast { _ ->
          if (fileExists) {
            var readmeText = ""
            file
              ?.bufferedReader()
              ?.use { readmeText = it.readText().replace(dsl.readme.regx, dsl.readme.replaced()) }
              ?.also { file?.writeText(readmeText) }
          }
        }
      }
    }

  companion object {
    const val README_TASK_NAME = "fillReadmeEnvVersion"
    const val LICENSE_TASK_NAME = "fillLicense"
  }
}
