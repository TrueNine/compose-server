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

import org.gradle.api.Project

open class ReadmeEnvRequirementFillerConfig(project: Project) {
  var codeName = "envRequirement"
  var kotlinVersion = KotlinVersion.CURRENT.toString()
  var javaVersion = System.getProperty("java.version")
  var gradleVersion = project.gradle.gradleVersion
  var prefix = "```"
  var suffix = "```"
  val regx: Regex
    get() {
      return "${prefix}${codeName}([^${suffix}]*)${suffix}".toRegex()
    }

  fun replaced(): String {
    return "${prefix}${codeName}\n${asEnvRequirementText()}${suffix}"
  }

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
