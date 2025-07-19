package io.github.truenine.composeserver.gradleplugin.entrance

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ConfigEntranceTest {

  private lateinit var project: Project
  private lateinit var configEntrance: ConfigEntrance

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().build()
    configEntrance = project.extensions.create(ConfigEntrance.DSL_NAME, ConfigEntrance::class.java, project)
  }

  @Test
  fun `should create config entrance with default values`() {
    assertNotNull(configEntrance.gradleGenerator)
    assertNotNull(configEntrance.jarExtension)
    assertNotNull(configEntrance.spotless)
    assertNotNull(configEntrance.dotenv)
    assertEquals(project, configEntrance.project)
  }

  @Test
  fun `should have correct DSL name`() {
    assertEquals("composeGradle", ConfigEntrance.DSL_NAME)
  }

  @Test
  fun `should configure gradle generator through DSL`() {
    var configuredEnabled = false

    configEntrance.gradleGenerator { config ->
      config.enabled = true
      configuredEnabled = config.enabled
    }

    assertTrue(configuredEnabled)
    assertTrue(configEntrance.gradleGenerator.enabled)
  }

  @Test
  fun `should configure jar extension through DSL`() {
    var configuredBootJarName = ""

    configEntrance.jarExtension { config ->
      config.bootJarName = "test-jar"
      configuredBootJarName = config.bootJarName
    }

    assertEquals("test-jar", configuredBootJarName)
    assertEquals("test-jar", configEntrance.jarExtension.bootJarName)
  }

  @Test
  fun `should configure spotless through DSL`() {
    var configuredEnabled = false

    configEntrance.spotless { config ->
      config.enabled = true
      configuredEnabled = config.enabled
    }

    assertTrue(configuredEnabled)
    assertTrue(configEntrance.spotless.enabled)
  }

  @Test
  fun `should configure dotenv through DSL`() {
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
  fun `should initialize with correct default configurations`() {
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

    // Test SpotlessConfig defaults
    val spotlessConfig = configEntrance.spotless
    assertEquals(false, spotlessConfig.enabled)

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
