package io.github.truenine.composeserver.gradleplugin

import io.github.truenine.composeserver.gradleplugin.entrance.ConfigEntrance
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MainTest {

  private lateinit var project: Project
  private lateinit var plugin: Main

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().build()
    plugin = Main()
  }

  @Test
  fun `should apply plugin successfully`() {
    plugin.apply(project)

    // Verify that the extension is created
    val extension = project.extensions.findByName(ConfigEntrance.DSL_NAME)
    assertNotNull(extension)
    assertTrue(extension is ConfigEntrance)
  }

  @Test
  fun `should create config entrance with correct name`() {
    plugin.apply(project)

    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    assertNotNull(configEntrance)
    assertNotNull(configEntrance.gradleGenerator)
    assertNotNull(configEntrance.jarExtension)
    assertNotNull(configEntrance.spotless)
  }

  @Test
  fun `should not create tasks when features are disabled`() {
    plugin.apply(project)

    // By default, all features are disabled, so no tasks should be created
    val gradleGeneratorTasks = project.tasks.matching { task -> task.group == "compose gradle" }

    // Tasks are only created when features are enabled
    assertTrue(gradleGeneratorTasks.isEmpty())
  }

  @Test
  fun `should handle project evaluation correctly`() {
    plugin.apply(project)

    // Enable jar extension
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.jarExtension.enabled = true

    // Verify that the configuration is accessible without errors
    assertTrue(configEntrance.jarExtension.enabled)
  }

  @Test
  fun `should configure gradle generator when enabled`() {
    plugin.apply(project)

    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.gradleGenerator.enabled = true

    // Verify configuration is accessible
    assertTrue(configEntrance.gradleGenerator.enabled)
  }

  @Test
  fun `should configure spotless when enabled`() {
    plugin.apply(project)

    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.spotless.enabled = true

    // Verify configuration is accessible
    assertTrue(configEntrance.spotless.enabled)
  }
}
