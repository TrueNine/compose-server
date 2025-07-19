package io.github.truenine.composeserver.gradleplugin.consts

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
}
