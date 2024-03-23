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
package net.yan100.compose.plugin.clean

import javax.inject.Inject
import net.yan100.compose.plugin.consts.Constant
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

class CleanExtension(
  @Inject private val project: Project,
  @Inject private val dsl: CleanExtensionConfig,
) {
  private val rootPath: String = project.project.rootDir.absolutePath
  private val deletes: Set<String>

  init {
    val clean = project.tasks["clean"]
    deletes = dsl.getAllProperties()
    val a =
      project.tasks.create<Delete>(TASK_NAME) {
        group = Constant.TASK_GROUP
        deletes.forEach { delete("${rootPath}/${it}") }
      }
    clean.dependsOn(a)
  }

  companion object {
    const val TASK_NAME = "cleanExtension"
    const val DSL_NAME = TASK_NAME
  }
}
