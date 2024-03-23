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
package net.yan100.compose.plugin.properties

import javax.inject.Inject
import net.yan100.compose.plugin.consts.Constant
import net.yan100.compose.plugin.wrap
import org.gradle.api.Project

/*
 * org.gradle.daemon=true
 * org.gradle.parallel=true
 * org.gradle.caching=false
 * org.gradle.jvmargs=-Xmx8192m -Xms4096m
 * org.gradle.workers.max=64
 */
class GradlePropertiesGenerator(
  @Inject private val project: Project,
  @Inject private val dsl: GradlePropertiesGeneratorConfig,
) {

  init {
    project.wrap {
      val propertiesFile = rootProject.layout.projectDirectory.file(GradlePropertiesGeneratorConfig.GRADLE_PROPERTIES_NAME)
      val file = propertiesFile.asFile
      tasks.create(TASK_NAME) {
        it.group = Constant.TASK_GROUP
        it.doLast {
          if (file.exists()) {
            file.writeText(dsl.toPropertiesString())
          } else {
            file.createNewFile()
            file.writeText(dsl.toPropertiesString())
          }
        }
      }
    }
  }

  companion object {
    const val TASK_NAME = "generateGradleProperties"
  }
}
