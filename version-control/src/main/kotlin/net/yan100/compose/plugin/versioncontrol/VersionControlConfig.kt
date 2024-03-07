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
package net.yan100.compose.plugin.versioncontrol

import javax.inject.Inject
import net.yan100.compose.plugin.clean.CleanExtensionConfig
import net.yan100.compose.plugin.publish.PublishExtensionConfig
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.JvmEcosystemPlugin
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create

interface EnhanceConfig {
    var languages: SetProperty<String>

    companion object {
        const val DSL_NAME = "enhancement"
    }
}

abstract class VersionControlConfig(@Inject val project: Project) {
    val cleanExtension: CleanExtensionConfig =
        project.extensions.create(CleanExtensionConfig.DSL_NAME, CleanExtensionConfig::class)
    val publishExtension: PublishExtensionConfig =
        project.extensions.create(PublishExtensionConfig.DSL_NAME, PublishExtensionConfig::class)

    /* === dsl === */

    val languages: SetProperty<String>
    val sourceSet: Property<SourceSet>
    val logger: org.gradle.api.logging.Logger
    val jvm = project.plugins.apply(JvmEcosystemPlugin::class)

    init {
        logger = project.logger
        languages = project.objects.setProperty(String::class.java).convention(listOf("java", "kotlin"))
        sourceSet = project.objects.property(SourceSet::class.java).convention(mainSourceSet(project))
    }

    private fun mainSourceSet(project: Project): SourceSet {
        return resolveSourceSet(SourceSet.MAIN_SOURCE_SET_NAME, project)
    }

    private fun resolveSourceSet(name: String, project: Project): SourceSet {
        val javaPluginExtension = project.extensions.getByType(JavaPluginExtension::class.java)
        return javaPluginExtension.sourceSets.getByName(name)
    }

    companion object {
        const val DSL_NAME = "versionControl"
    }
}
