package io.github.truenine.composeserver.gradleplugin

import kotlin.test.*
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FunctionsTest {

  private lateinit var project: Project

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().build()
    project.plugins.apply(JavaPlugin::class.java)
  }

  @Test
  fun `wrap_function_provides_GradleProjectDelegator_context`() {
    var delegatorName = ""
    var isRoot = false

    project.wrap {
      delegatorName = this.name
      isRoot = this.isRootProject
    }

    assertEquals(project.name, delegatorName)
    assertTrue(isRoot) // Single project is considered root
  }

  @Test
  fun `wrap_function_provides_access_to_source_sets`() {
    var hasMainSourceSet = false
    var hasTestSourceSet = false

    project.wrap {
      hasMainSourceSet = this.sourceSets.findByName("main") != null
      hasTestSourceSet = this.sourceSets.findByName("test") != null
    }

    assertTrue(hasMainSourceSet)
    assertTrue(hasTestSourceSet)
  }

  @Test
  fun `wrap_function_provides_access_to_resources`() {
    var mainResourcesExists = false
    var testResourcesExists = false

    project.wrap {
      mainResourcesExists = this.mainResources != null
      testResourcesExists = this.testResources != null
    }

    assertTrue(mainResourcesExists)
    assertTrue(testResourcesExists)
  }

  @Test
  fun `wrap_function_provides_access_to_logger`() {
    var loggerName = ""

    project.wrap { loggerName = this.log.name }

    assertNotNull(loggerName)
    assertEquals(project.logger.name, loggerName)
  }

  @Test
  fun `wrap_function_returns_value_from_lambda`() {
    val result = project.wrap { "test-result" }

    assertEquals("test-result", result)
  }

  @Test
  fun `wrap_function_handles_complex_operations`() {
    val result =
      project.wrap {
        val mainSourceSet = this.sourceSets.findByName("main")
        mainSourceSet?.name ?: "not-found"
      }

    assertEquals("main", result)
  }
}
