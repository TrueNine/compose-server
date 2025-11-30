package io.github.truenine.composeserver.gradleplugin

import io.github.truenine.composeserver.gradleplugin.consts.Constant
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GradleFnsTest {

  private lateinit var project: Project

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().build()
  }

  @Test
  fun `emptyVersion_returns_empty_string_for_unspecified_version`() {
    // Default project version is "unspecified"
    assertEquals("", project.emptyVersion)
  }

  @Test
  fun `emptyVersion_returns_actual_version_when_specified`() {
    project.version = "1.2.3"
    assertEquals("1.2.3", project.emptyVersion)
  }

  @Test
  fun `emptyVersion_returns_empty_string_for_empty_version`() {
    project.version = ""
    assertEquals("", project.emptyVersion)
  }

  @Test
  fun `emptyVersion_handles_null_version`() {
    project.version = "unspecified" // Gradle doesn't allow null version, use unspecified instead
    assertEquals("", project.emptyVersion)
  }

  @Test
  fun `emptyVersion_returns_version_for_any_non_unspecified_value`() {
    project.version = "SNAPSHOT"
    assertEquals("SNAPSHOT", project.emptyVersion)

    project.version = "1.0-BETA"
    assertEquals("1.0-BETA", project.emptyVersion)

    project.version = "dev"
    assertEquals("dev", project.emptyVersion)
  }

  @Test
  fun `verify_UNKNOWN_PROJECT_VERSION_constant`() {
    assertEquals("unspecified", Constant.Gradle.UNKNOWN_PROJECT_VERSION)
  }
}
