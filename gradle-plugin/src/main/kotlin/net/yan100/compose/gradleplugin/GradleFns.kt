package net.yan100.compose.gradleplugin

import java.net.URI
import net.yan100.compose.gradleplugin.consts.Constant
import net.yan100.compose.gradleplugin.consts.Repos
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Provider

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
