package io.github.truenine.composeserver.gradleplugin.jar

import kotlin.test.assertEquals
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JarExtensionConfigTest {

  private lateinit var project: Project
  private lateinit var config: JarExtensionConfig

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().build()
    config = JarExtensionConfig(project)
  }

  @Test
  fun `should have correct default values`() {
    assertEquals(false, config.enabled)
    assertEquals("lib", config.bootJarDistName)
    assertEquals("config", config.bootJarConfigName)
    assertEquals(project.name, config.bootJarName)
    assertEquals("1.0", config.defaultVersion)
    assertEquals("libs", config.jarDistDir)
    assertEquals(false, config.bootJarSeparate)
    assertEquals("boot", config.bootJarClassifier)
    assertEquals(true, config.copyLicense)
  }

  @Test
  fun `should use project version when available`() {
    // Set project version
    project.version = "2.0.0"
    val configWithVersion = JarExtensionConfig(project)

    assertEquals("2.0.0", configWithVersion.bootJarVersion)
  }

  @Test
  fun `should use default version when project version is unspecified`() {
    // Project version is "unspecified" by default
    assertEquals("1.0", config.bootJarVersion)
  }

  @Test
  fun `should use default version when project version is empty`() {
    project.version = ""
    val configWithEmptyVersion = JarExtensionConfig(project)

    assertEquals("1.0", configWithEmptyVersion.bootJarVersion)
  }

  @Test
  fun `should allow configuration changes`() {
    config.enabled = true
    config.bootJarDistName = "dependencies"
    config.bootJarConfigName = "configuration"
    config.bootJarName = "custom-app"
    config.defaultVersion = "2.0"
    config.jarDistDir = "build/libs"
    config.bootJarVersion = "3.0.0"
    config.bootJarSeparate = true
    config.bootJarClassifier = "executable"
    config.copyLicense = false

    assertEquals(true, config.enabled)
    assertEquals("dependencies", config.bootJarDistName)
    assertEquals("configuration", config.bootJarConfigName)
    assertEquals("custom-app", config.bootJarName)
    assertEquals("2.0", config.defaultVersion)
    assertEquals("build/libs", config.jarDistDir)
    assertEquals("3.0.0", config.bootJarVersion)
    assertEquals(true, config.bootJarSeparate)
    assertEquals("executable", config.bootJarClassifier)
    assertEquals(false, config.copyLicense)
  }

  @Test
  fun `should handle project name correctly`() {
    val namedProject = ProjectBuilder.builder().withName("test-project").build()
    val namedConfig = JarExtensionConfig(namedProject)

    assertEquals("test-project", namedConfig.bootJarName)
  }
}
