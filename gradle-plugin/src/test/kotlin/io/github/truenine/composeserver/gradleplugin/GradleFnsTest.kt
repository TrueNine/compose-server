package io.github.truenine.composeserver.gradleplugin

import io.github.truenine.composeserver.gradleplugin.consts.Constant
import kotlin.test.assertEquals
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GradleFnsTest {

  private lateinit var project: Project

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().build()
  }

  @Test
  fun `emptyVersion should return empty string for unspecified version`() {
    // Default project version is "unspecified"
    assertEquals("", project.emptyVersion)
  }

  @Test
  fun `emptyVersion should return actual version when specified`() {
    project.version = "1.2.3"
    assertEquals("1.2.3", project.emptyVersion)
  }

  @Test
  fun `emptyVersion should return empty string for empty version`() {
    project.version = ""
    assertEquals("", project.emptyVersion)
  }

  @Test
  fun `emptyVersion should handle null version`() {
    project.version = "unspecified" // Gradle doesn't allow null version, use unspecified instead
    assertEquals("", project.emptyVersion)
  }

  @Test
  fun `emptyVersion should return version for any non-unspecified value`() {
    project.version = "SNAPSHOT"
    assertEquals("SNAPSHOT", project.emptyVersion)

    project.version = "1.0-BETA"
    assertEquals("1.0-BETA", project.emptyVersion)

    project.version = "dev"
    assertEquals("dev", project.emptyVersion)
  }

  @Test
  fun `should verify UNKNOWN_PROJECT_VERSION constant`() {
    assertEquals("unspecified", Constant.Gradle.UNKNOWN_PROJECT_VERSION)
  }
}
