package io.github.truenine.composeserver.gradleplugin.dotenv

import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test as JUnitTest
import org.junit.jupiter.api.io.TempDir
import org.springframework.boot.gradle.tasks.run.BootRun

class DotenvLoaderTest {

  @TempDir lateinit var tempDir: File

  private lateinit var project: Project
  private lateinit var config: DotenvConfig

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().withProjectDir(tempDir).build()

    // Apply Spring Boot plugin to get BootRun task
    project.pluginManager.apply("org.springframework.boot")

    config = DotenvConfig()
  }

  @JUnitTest
  fun `not_load_when_config_is_invalid`() {
    config.enabled = false

    val loader = DotenvLoader(project, config)

    assertTrue(loader.getLoadedVariables().isEmpty())
  }

  @JUnitTest
  fun `handle_missing_dotenv_file_gracefully`() {
    config.enabled = true
    config.filePath = "nonexistent.env"
    config.warnOnMissingFile = false

    val loader = DotenvLoader(project, config)

    assertTrue(loader.getLoadedVariables().isEmpty())
  }

  @JUnitTest
  fun `parse_simple_key_value_pairs`() {
    val envFile =
      createTempEnvFile(
        """
        KEY1=value1
        KEY2=value2
        KEY3=value3
        """
          .trimIndent()
      )

    config.enabled = true
    config.filePath = envFile.absolutePath

    val loader = DotenvLoader(project, config)
    val variables = loader.getLoadedVariables()

    assertEquals(3, variables.size)
    assertEquals("value1", variables["KEY1"])
    assertEquals("value2", variables["KEY2"])
    assertEquals("value3", variables["KEY3"])
  }

  @JUnitTest
  fun `handle_quoted_values_correctly`() {
    val envFile =
      createTempEnvFile(
        """
        SINGLE_QUOTED='single value'
        DOUBLE_QUOTED="double value"
        ESCAPED_QUOTES="value with \"quotes\""
        SPECIAL_CHARS="line1\nline2\ttab"
        """
          .trimIndent()
      )

    config.enabled = true
    config.filePath = envFile.absolutePath

    val loader = DotenvLoader(project, config)
    val variables = loader.getLoadedVariables()

    assertEquals("single value", variables["SINGLE_QUOTED"])
    assertEquals("double value", variables["DOUBLE_QUOTED"])
    assertEquals("value with \"quotes\"", variables["ESCAPED_QUOTES"])
    assertEquals("line1\nline2\ttab", variables["SPECIAL_CHARS"])
  }

  @JUnitTest
  fun `skip_comments_and_empty_lines`() {
    val envFile =
      createTempEnvFile(
        """
        # This is a comment
        KEY1=value1

        # Another comment
        KEY2=value2

        KEY3=value3
        """
          .trimIndent()
      )

    config.enabled = true
    config.filePath = envFile.absolutePath

    val loader = DotenvLoader(project, config)
    val variables = loader.getLoadedVariables()

    assertEquals(3, variables.size)
    assertEquals("value1", variables["KEY1"])
    assertEquals("value2", variables["KEY2"])
    assertEquals("value3", variables["KEY3"])
  }

  @JUnitTest
  fun `apply_prefix_filter`() {
    val envFile =
      createTempEnvFile(
        """
        APP_KEY1=value1
        APP_KEY2=value2
        OTHER_KEY=value3
        KEY4=value4
        """
          .trimIndent()
      )

    config.enabled = true
    config.filePath = envFile.absolutePath
    config.prefixFilter = "APP_"

    val loader = DotenvLoader(project, config)
    val variables = loader.getLoadedVariables()

    assertEquals(2, variables.size)
    assertEquals("value1", variables["APP_KEY1"])
    assertEquals("value2", variables["APP_KEY2"])
    assertFalse(variables.containsKey("OTHER_KEY"))
    assertFalse(variables.containsKey("KEY4"))
  }

  @JUnitTest
  fun `exclude_specified_keys`() {
    val envFile =
      createTempEnvFile(
        """
        KEY1=value1
        SECRET_KEY=secret
        KEY2=value2
        PASSWORD=password
        """
          .trimIndent()
      )

    config.enabled = true
    config.filePath = envFile.absolutePath
    config.excludeKeys("SECRET_KEY", "PASSWORD")

    val loader = DotenvLoader(project, config)
    val variables = loader.getLoadedVariables()

    assertEquals(2, variables.size)
    assertEquals("value1", variables["KEY1"])
    assertEquals("value2", variables["KEY2"])
    assertFalse(variables.containsKey("SECRET_KEY"))
    assertFalse(variables.containsKey("PASSWORD"))
  }

  @JUnitTest
  fun `include_only_specified_keys`() {
    val envFile =
      createTempEnvFile(
        """
        KEY1=value1
        KEY2=value2
        KEY3=value3
        KEY4=value4
        """
          .trimIndent()
      )

    config.enabled = true
    config.filePath = envFile.absolutePath
    config.includeKeys("KEY1", "KEY3")

    val loader = DotenvLoader(project, config)
    val variables = loader.getLoadedVariables()

    assertEquals(2, variables.size)
    assertEquals("value1", variables["KEY1"])
    assertEquals("value3", variables["KEY3"])
    assertFalse(variables.containsKey("KEY2"))
    assertFalse(variables.containsKey("KEY4"))
  }

  @JUnitTest
  fun `ignore_empty_values_when_configured`() {
    val envFile =
      createTempEnvFile(
        """
        KEY1=value1
        EMPTY_KEY=
        KEY2=value2
        BLANK_KEY=""
        """
          .trimIndent()
      )

    config.enabled = true
    config.filePath = envFile.absolutePath
    config.ignoreEmptyValues = true

    val loader = DotenvLoader(project, config)
    val variables = loader.getLoadedVariables()

    assertEquals(2, variables.size)
    assertEquals("value1", variables["KEY1"])
    assertEquals("value2", variables["KEY2"])
    assertFalse(variables.containsKey("EMPTY_KEY"))
    assertFalse(variables.containsKey("BLANK_KEY"))
  }

  @JUnitTest
  fun `inject_variables_into_test_tasks`() {
    val envFile =
      createTempEnvFile(
        """
        TEST_VAR=test_value
        DB_HOST=localhost
        """
          .trimIndent()
      )

    config.enabled = true
    config.filePath = envFile.absolutePath

    // Create test task
    val testTask = project.tasks.create("test", Test::class.java)

    val loader = DotenvLoader(project, config)

    // Verify environment variables injected
    assertEquals("test_value", testTask.environment["TEST_VAR"])
    assertEquals("localhost", testTask.environment["DB_HOST"])
  }

  @JUnitTest
  fun `inject_variables_into_bootrun_tasks`() {
    val envFile =
      createTempEnvFile(
        """
        SPRING_PROFILE=dev
        SERVER_PORT=8080
        """
          .trimIndent()
      )

    config.enabled = true
    config.filePath = envFile.absolutePath

    // Create BootRun task
    val bootRunTask = project.tasks.create("bootRun", BootRun::class.java)

    val loader = DotenvLoader(project, config)

    // Verify environment variables injected
    assertEquals("dev", bootRunTask.environment["SPRING_PROFILE"])
    assertEquals("8080", bootRunTask.environment["SERVER_PORT"])
  }

  @JUnitTest
  fun `inject_variables_into_javaexec_tasks`() {
    val envFile =
      createTempEnvFile(
        """
        CUSTOM_OPTS=-Xmx1g
        APP_ENV=production
        DATABASE_URL=jdbc:postgresql://localhost:5432/mydb
        """
          .trimIndent()
      )

    config.enabled = true
    config.filePath = envFile.absolutePath

    // Create JavaExec task
    val javaExecTask = project.tasks.create("runApp", JavaExec::class.java)

    val loader = DotenvLoader(project, config)

    // Verify environment variables injected
    assertEquals("-Xmx1g", javaExecTask.environment["CUSTOM_OPTS"])
    assertEquals("production", javaExecTask.environment["APP_ENV"])
    assertEquals("jdbc:postgresql://localhost:5432/mydb", javaExecTask.environment["DATABASE_URL"])
  }

  @JUnitTest
  fun `inject_variables_into_kotlin_run_task`() {
    val envFile =
      createTempEnvFile(
        """
        KOTLIN_ENV=development
        DEBUG_MODE=true
        """
          .trimIndent()
      )

    config.enabled = true
    config.filePath = envFile.absolutePath

    // Create run task for Kotlin application (JavaExec type)
    val runTask = project.tasks.create("run", JavaExec::class.java)

    val loader = DotenvLoader(project, config)

    // Verify environment variables injected
    assertEquals("development", runTask.environment["KOTLIN_ENV"])
    assertEquals("true", runTask.environment["DEBUG_MODE"])
  }

  private fun createTempEnvFile(content: String): File {
    val envFile = File(tempDir, ".env")
    envFile.writeText(content)
    return envFile
  }
}
