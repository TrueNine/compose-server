package io.github.truenine.composeserver.gradleplugin.entrance

import io.github.truenine.composeserver.gradleplugin.dotenv.DotenvConfig
import io.github.truenine.composeserver.gradleplugin.generator.GradleGeneratorConfig
import io.github.truenine.composeserver.gradleplugin.jar.JarExtensionConfig
import io.github.truenine.composeserver.gradleplugin.spotless.SpotlessConfig
import javax.inject.Inject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

abstract class ConfigEntrance(@Inject val project: Project) : ExtensionAware {
  val gradleGenerator = GradleGeneratorConfig()
  val jarExtension = JarExtensionConfig(project)
  val spotless = SpotlessConfig()
  val dotenv = DotenvConfig()

  /** ## spotless 扩展配置 */
  fun spotless(action: Action<SpotlessConfig>) = action.execute(spotless)

  /**
   * ## jar 打包扩展配置
   *
   * @param action 打包配置
   */
  fun jarExtension(action: Action<JarExtensionConfig>) = action.execute(jarExtension)

  /**
   * ## gradle properties 生成扩展配置
   *
   * @param action 生成配置
   */
  fun gradleGenerator(action: Action<GradleGeneratorConfig>) = action.execute(gradleGenerator)

  /**
   * ## dotenv 环境变量加载扩展配置
   *
   * @param action dotenv 配置
   */
  fun dotenv(action: Action<DotenvConfig>) = action.execute(dotenv)

  companion object {
    const val DSL_NAME = "composeGradle"
  }
}
