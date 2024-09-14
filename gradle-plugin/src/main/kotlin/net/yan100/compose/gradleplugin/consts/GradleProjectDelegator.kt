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
package net.yan100.compose.gradleplugin.consts

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.SourceSetContainer

open class GradleProjectDelegator(project: Project) : Project by project, ExtensionAware {
  val isRootProject: Boolean
    get() = null == parent && project == rootProject

  val sourceSets
    get() = extensions.getByName("sourceSets") as SourceSetContainer

  fun sourceSets(configure: Action<SourceSetContainer>) = extensions.configure("sourceSets", configure)

  val mainResources
    get() = sourceSets.findByName("main")?.resources

  val testResources
    get() = sourceSets.findByName("test")?.resources

  val log = project.logger
  val projectConfig = ProjectConfig(this.project)

  inner class ProjectConfig(private val project: Project) : Project by project {
    val configDir = rootProject.layout.projectDirectory.dir(Constant.Config.CONFIG_DIR)
    val licenseMetaFile = configDir.file(Constant.Config.LICENSE_META)
  }
}
