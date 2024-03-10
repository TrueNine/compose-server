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

import net.yan100.compose.plugin.wrap
import org.gradle.api.Action
import org.gradle.api.Project
import java.time.LocalDate

open class FillerConfig(private val project: Project) {
  var readme = ReadmeConfig(project)
  var license = LicenseConfig(project)

  fun license(action: Action<LicenseConfig>) = action.execute(license)

  fun readme(action: Action<ReadmeConfig>) = action.execute(readme)

  inner class ReadmeConfig(project: Project) {
    init {
      // TODO 从gradle中获取kotlin版本
      if (false) {
        project.wrap {
          configurations
            .findByName("implementation")
            ?.resolvedConfiguration
            ?.resolvedArtifacts
            ?.find {
              it.moduleVersion.id.version
              true
            }
        }
      }
    }

    var codeName = "envRequirement"
    var kotlinVersion = project
    var javaVersion = System.getProperty("java.version")
    var gradleVersion = project.gradle.gradleVersion
    var prefix = "```"
    var suffix = "```"
    val regx: Regex
      get() = "${prefix}${codeName}([^${suffix}]*)${suffix}".toRegex()

    fun replaced(): String = "${prefix}${codeName}\n${asEnvRequirementText()}${suffix}"

    /** ## 生成环境要求文本 */
    fun asEnvRequirementText(): String {
      return buildString {
        appendLine("java: $javaVersion")
        appendLine("kotlin: $kotlinVersion")
        appendLine("gradle: $gradleVersion")
      }
    }

    /** ## 设置独立的后缀 */
    fun suffix(suffix: String) {
      this.suffix = suffix
    }

    /** ## 设置独立的前缀 */
    fun prefix(prefix: String) {
      this.prefix = prefix
    }

    /** ## 配置要填充的代码名称 */
    fun codeName(codeName: String) {
      this.codeName = codeName
    }
  }

  inner class LicenseConfig(project: Project) : MutableMap<String, String> by mutableMapOf() {
    /** ## 是否留下行尾换行 */
    var newLine: Boolean = true

    init {
      put("NOW_YEAR", LocalDate.now().year.toString())
      put("NOW_MONTH", LocalDate.now().monthValue.toString().padStart(2, '0'))
      put("NOW_DAY", LocalDate.now().dayOfMonth.toString().padStart(2, '0'))
    }

    fun website(website: String) {
      this["WEBSITE"] = website
    }

    fun email(email: String) {
      this["EMAIL"] = email
    }

    fun license(license: String) {
      this["LICENSE"] = license
    }

    fun author(author: String) {
      this["AUTHOR"] = author
    }
  }
}
