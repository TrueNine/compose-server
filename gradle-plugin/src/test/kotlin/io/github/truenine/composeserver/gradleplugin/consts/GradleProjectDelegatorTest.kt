package io.github.truenine.composeserver.gradleplugin.consts

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GradleProjectDelegatorTest {

  private lateinit var project: Project
  private lateinit var delegator: GradleProjectDelegator

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().build()
    project.plugins.apply(JavaPlugin::class.java)
    delegator = GradleProjectDelegator(project)
  }

  @Test
  fun `delegate_to_project_correctly`() {
    assertEquals(project.name, delegator.name)
    assertEquals(project.version, delegator.version)
    assertEquals(project.group, delegator.group)
  }

  @Test
  fun `identify_root_project_correctly`() {
    val rootProject = ProjectBuilder.builder().build()
    val rootDelegator = GradleProjectDelegator(rootProject)
    assertTrue(rootDelegator.isRootProject)

    val childProject = ProjectBuilder.builder().withParent(rootProject).build()
    val childDelegator = GradleProjectDelegator(childProject)
    assertEquals(false, childDelegator.isRootProject)
  }

  @Test
  fun `provide_access_to_source_sets`() {
    assertNotNull(delegator.sourceSets)
    assertNotNull(delegator.sourceSets.findByName("main"))
    assertNotNull(delegator.sourceSets.findByName("test"))
  }

  @Test
  fun `provide_access_to_main_resources`() {
    assertNotNull(delegator.mainResources)
  }

  @Test
  fun `provide_access_to_test_resources`() {
    assertNotNull(delegator.testResources)
  }

  @Test
  fun `provide_access_to_logger`() {
    assertNotNull(delegator.log)
    assertEquals(project.logger, delegator.log)
  }

  @Test
  fun `configure_source_sets_through_dsl`() {
    var configuredSourceSets = false

    delegator.sourceSets { sourceSets ->
      assertNotNull(sourceSets.findByName("main"))
      configuredSourceSets = true
    }

    assertTrue(configuredSourceSets)
  }
}
