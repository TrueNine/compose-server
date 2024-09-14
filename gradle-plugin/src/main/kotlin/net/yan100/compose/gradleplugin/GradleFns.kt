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
package net.yan100.compose.gradleplugin

import net.yan100.compose.gradleplugin.consts.Constant
import net.yan100.compose.gradleplugin.consts.Repos
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.wrapper.Wrapper
import java.net.URI

fun RepositoryHandler.chinaRegionRepositories() {
  Repos.publicRepositories.forEach { url -> this.maven { it.url = URI(url) } }
}

/** 排除指定的 catalog 依赖 */
fun ModuleDependency.exclude(dep: Provider<MinimalExternalModuleDependency>) {
  exclude(mutableMapOf("group" to dep.get().module.group, "module" to dep.get().module.name))
}

/**
 * ## 返回此项目的版本号，
 *
 * @return 如果版本号为 [Constant.Gradle.UNKNOWN_PROJECT_VERSION] 则返回空字符串
 */
val Project.emptyVersion: String
  get() = if (this.project.version.toString() == Constant.Gradle.UNKNOWN_PROJECT_VERSION) "" else this.project.version.toString()
