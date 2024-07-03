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
package net.yan100.compose.plugin.jar

import net.yan100.compose.plugin.emptyVersion
import org.gradle.api.Project

open class JarExtensionConfig(project: Project) {
  var enabled: Boolean = false
  var bootJarDistName: String = "lib"
  var bootJarConfigName: String = "config"
  var bootJarName: String = project.name
  var defaultVersion: String = "1.0"
  var jarDistDir = "libs"
  var bootJarVersion: String = if (project.emptyVersion.isEmpty()) project.emptyVersion else defaultVersion
  var bootJarSeparate: Boolean = false
  var bootJarClassifier: String = "boot"
  var copyLicense: Boolean = true
}
