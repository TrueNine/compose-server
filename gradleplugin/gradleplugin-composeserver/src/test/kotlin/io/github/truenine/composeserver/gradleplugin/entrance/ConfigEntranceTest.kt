package io.github.truenine.composeserver.gradleplugin.entrance

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class ConfigEntranceTest {

  private lateinit var project: Project
  private lateinit var configEntrance: ConfigEntrance

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().build()
    configEntrance = project.extensions.create(ConfigEntrance.DSL_NAME, ConfigEntrance::class.java, project)
  }

  @Test
  fun `create_config_entrance_with_default_values`() {
    assertNotNull(configEntrance.gradleGenerator)
    assertNotNull(configEntrance.jarExtension)
    assertNotNull(configEntrance.dotenv)
    assertEquals(project, configEntrance.project)
  }

  @Test
  fun `have_correct_dsl_name`() {
    assertEquals("composeGradle", ConfigEntrance.DSL_NAME)
  }

  @Test
  fun `configure_jar_extension_through_dsl`() {
    var configuredBootJarName = ""

    configEntrance.jarExtension { config ->
      config.bootJarName = "test-jar"
      configuredBootJarName = config.bootJarName
    }

    assertEquals("test-jar", configuredBootJarName)
    assertEquals("test-jar", configEntrance.jarExtension.bootJarName)
  }

  @Test
  fun `configure_dotenv_through_dsl`() {
    var configuredFilePath = ""

    configEntrance.dotenv { config ->
      config.enabled = true
      config.filePath = ".env"
      configuredFilePath = config.filePath
    }

    assertEquals(".env", configuredFilePath)
    assertTrue(configEntrance.dotenv.enabled)
    assertEquals(".env", configEntrance.dotenv.filePath)
  }

  @Test
  fun `initialize_with_correct_default_configurations`() {
    // Test GradleGeneratorConfig defaults
    val gradleConfig = configEntrance.gradleGenerator
    assertEquals(false, gradleConfig.enabled)
    assertNotNull(gradleConfig.initGradle)

    // Test JarExtensionConfig defaults
    val jarConfig = configEntrance.jarExtension
    assertEquals(false, jarConfig.enabled)
    assertEquals("lib", jarConfig.bootJarDistName)
    assertEquals("config", jarConfig.bootJarConfigName)
    assertEquals(project.name, jarConfig.bootJarName)
    assertEquals("1.0", jarConfig.defaultVersion)
    assertEquals("libs", jarConfig.jarDistDir)
    assertEquals(false, jarConfig.bootJarSeparate)
    assertEquals("boot", jarConfig.bootJarClassifier)
    assertEquals(true, jarConfig.copyLicense)

    // Test DotenvConfig defaults
    val dotenvConfig = configEntrance.dotenv
    assertEquals(false, dotenvConfig.enabled)
    assertEquals("", dotenvConfig.filePath)
    assertEquals(true, dotenvConfig.warnOnMissingFile)
    assertEquals(true, dotenvConfig.verboseErrors)
    assertEquals(false, dotenvConfig.overrideExisting)
    assertEquals(false, dotenvConfig.ignoreEmptyValues)
  }
}
