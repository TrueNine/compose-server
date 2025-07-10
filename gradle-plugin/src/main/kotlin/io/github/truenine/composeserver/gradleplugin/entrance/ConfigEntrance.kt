package io.github.truenine.composeserver.gradleplugin.entrance

import io.github.truenine.composeserver.gradleplugin.generator.GradleGeneratorConfig
import io.github.truenine.composeserver.gradleplugin.jar.JarExtensionConfig
import io.github.truenine.composeserver.gradleplugin.spotless.SpotlessConfig
import javax.inject.Inject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.JvmEcosystemPlugin
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.apply

abstract class ConfigEntrance(@Inject val project: Project) : ExtensionAware {
  val gradleGenerator = GradleGeneratorConfig()
  val jarExtension = JarExtensionConfig(project)
  val spotless = SpotlessConfig()

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
    const val DSL_NAME = "composeGradle"
  }
}
