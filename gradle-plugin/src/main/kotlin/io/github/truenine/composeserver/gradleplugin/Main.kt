package io.github.truenine.composeserver.gradleplugin

import io.github.truenine.composeserver.gradleplugin.dotenv.DotenvLoader
import io.github.truenine.composeserver.gradleplugin.entrance.ConfigEntrance
import io.github.truenine.composeserver.gradleplugin.generator.GradleGenerator
import io.github.truenine.composeserver.gradleplugin.jar.JarExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class Main : Plugin<Project> {
  override fun apply(project: Project) {
    val cfg = project.extensions.create<ConfigEntrance>(ConfigEntrance.DSL_NAME, project)

    if (cfg.gradleGenerator.enabled) {
      val gradleGenerator = GradleGenerator(project, cfg.gradleGenerator)
    }

    // 环境变量加载需要在项目评估后执行，以确保配置已完全加载
    project.afterEvaluate { s ->
      if (cfg.dotenv.enabled) {
        val dotenvLoader = DotenvLoader(s, cfg.dotenv)
      }

      if (cfg.jarExtension.enabled) {
        s.wrap {
          val jarExtension = JarExtension(this, cfg.jarExtension)
        }
      }
    }
  }
}
