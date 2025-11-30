package io.github.truenine.composeserver.gradleplugin

import io.github.truenine.composeserver.gradleplugin.consts.*
import java.net.URI
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Provider

fun RepositoryHandler.chinaRegionRepositories() {
  MavenRepositoryUrls.publicRepositories.forEach { url -> this.maven { it.url = URI(url) } }
}

/** Exclude specified catalog dependency */
fun ModuleDependency.exclude(dep: Provider<MinimalExternalModuleDependency>) {
  exclude(mutableMapOf("group" to dep.get().module.group, "module" to dep.get().module.name))
}

/**
 * ## Return the version of this project.
 *
 * @return Empty string if the version equals [Constant.Gradle.UNKNOWN_PROJECT_VERSION]
 */
val Project.emptyVersion: String
  get() = if (this.project.version.toString() == Constant.Gradle.UNKNOWN_PROJECT_VERSION) "" else this.project.version.toString()

inline fun <R> Project.wrap(crossinline action: GradleProjectDelegator.() -> R): R {
  return action(GradleProjectDelegator(this))
}
