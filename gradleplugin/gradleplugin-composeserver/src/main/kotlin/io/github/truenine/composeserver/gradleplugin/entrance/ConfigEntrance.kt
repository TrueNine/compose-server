package io.github.truenine.composeserver.gradleplugin.entrance

import io.github.truenine.composeserver.gradleplugin.dotenv.DotenvConfig
import io.github.truenine.composeserver.gradleplugin.generator.GradleGeneratorConfig
import io.github.truenine.composeserver.gradleplugin.jar.JarExtensionConfig
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import javax.inject.Inject

abstract class ConfigEntrance(@Inject val project: Project) : ExtensionAware {
  val gradleGenerator = GradleGeneratorConfig()
  val jarExtension = JarExtensionConfig(project)
  val dotenv = DotenvConfig()

  /**
   * ## Jar packaging extension configuration
   *
   * @param action jar packaging configuration
   */
  fun jarExtension(action: Action<JarExtensionConfig>) = action.execute(jarExtension)

  /**
   * ## gradle.properties generation extension configuration
   *
   * @param action generator configuration
   */
  fun gradleGenerator(action: Action<GradleGeneratorConfig>) = action.execute(gradleGenerator)

  /**
   * ## dotenv environment variable loading extension configuration
   *
   * @param action dotenv configuration
   */
  fun dotenv(action: Action<DotenvConfig>) = action.execute(dotenv)

  companion object {
    const val DSL_NAME = "composeGradle"
  }
}
