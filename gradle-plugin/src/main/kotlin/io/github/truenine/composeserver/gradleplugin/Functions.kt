package io.github.truenine.composeserver.gradleplugin

import net.yan100.compose.gradleplugin.consts.GradleProjectDelegator
import org.gradle.api.Project

inline fun <R> Project.wrap(crossinline action: GradleProjectDelegator.() -> R): R {
  return action(GradleProjectDelegator(this))
}
