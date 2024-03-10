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
package net.yan100.compose.plugin.consts

object Constant {
  const val TASK_GROUP = "compose gradle"

  object Internal {
    const val META_INIT_GRADLE_KTS = "meta.init.gradle.kts"
  }

  object Config {
    const val CONFIG_DIR = ".compose-config"
    const val LICENSE_META = "license.meta"
  }

  object FileName {
    const val LICENSE = "LICENSE"
    const val README = "README.md"
  }

  object FileNameSet {
    val README = setOf(FileName.README, "readme.txt", "readme").map { it.lowercase() }
    val LICENSE = setOf("license", "license.txt", FileName.LICENSE).map { it.lowercase() }
  }

  object Gradle {
    const val UNKNOWN_PROJECT_VERSION = "unspecified"
  }

  object PluginId {
    const val MAVEN_PUBLISH = "maven-publish"
  }
}
