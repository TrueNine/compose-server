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
package net.yan100.compose.plugin.publish

import javax.inject.Inject
import net.yan100.compose.plugin.consts.Constant
import net.yan100.compose.plugin.wrap
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven

class PublishExtension(
  @Inject private val project: Project,
  @Inject private val dsl: PublishExtensionConfig,
) {
  init {
    project.wrap {
      val hasMavenPlugin = plugins.hasPlugin(Constant.PluginId.MAVEN_PUBLISH)
      if (hasMavenPlugin) {
        extensions.getByType<PublishingExtension>().repositories { it.maven(url = layout.buildDirectory.dir(PublishExtensionConfig.DEFAULT_LOCAL_NAME)) }
      }
    }
  }
}