package io.github.truenine.composeserver.gradleplugin

import io.github.truenine.composeserver.gradleplugin.entrance.ConfigEntrance
import io.github.truenine.composeserver.gradleplugin.generator.GradleGenerator
import io.github.truenine.composeserver.gradleplugin.jar.JarExtension
import io.github.truenine.composeserver.gradleplugin.spotless.Spotless
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class Main : Plugin<Project> {
  override fun apply(project: Project) {
    val cfg = project.extensions.create<ConfigEntrance>(ConfigEntrance.DSL_NAME, project)

    if (cfg.gradleGenerator.enabled) {
      val gradleGenerator = GradleGenerator(project, cfg.gradleGenerator)
    }

    if (cfg.spotless.enabled) {
      val spotless = Spotless(project, cfg.spotless)
    }

    project.afterEvaluate { s ->
      if (cfg.jarExtension.enabled) {
        s.wrap {
          val jarExtension = JarExtension(this, cfg.jarExtension)
        }
      }
    }
  }
}
