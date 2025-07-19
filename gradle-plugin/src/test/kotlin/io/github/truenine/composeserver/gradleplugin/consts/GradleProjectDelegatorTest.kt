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
  fun `should delegate to project correctly`() {
    assertEquals(project.name, delegator.name)
    assertEquals(project.version, delegator.version)
    assertEquals(project.group, delegator.group)
  }

  @Test
  fun `should identify root project correctly`() {
    val rootProject = ProjectBuilder.builder().build()
    val rootDelegator = GradleProjectDelegator(rootProject)
    assertTrue(rootDelegator.isRootProject)

    val childProject = ProjectBuilder.builder().withParent(rootProject).build()
    val childDelegator = GradleProjectDelegator(childProject)
    assertEquals(false, childDelegator.isRootProject)
  }

  @Test
  fun `should provide access to source sets`() {
    assertNotNull(delegator.sourceSets)
    assertNotNull(delegator.sourceSets.findByName("main"))
    assertNotNull(delegator.sourceSets.findByName("test"))
  }

  @Test
  fun `should provide access to main resources`() {
    assertNotNull(delegator.mainResources)
  }

  @Test
  fun `should provide access to test resources`() {
    assertNotNull(delegator.testResources)
  }

  @Test
  fun `should provide access to logger`() {
    assertNotNull(delegator.log)
    assertEquals(project.logger, delegator.log)
  }

  @Test
  fun `should configure source sets through DSL`() {
    var configuredSourceSets = false

    delegator.sourceSets { sourceSets ->
      assertNotNull(sourceSets.findByName("main"))
      configuredSourceSets = true
    }

    assertTrue(configuredSourceSets)
  }
}
