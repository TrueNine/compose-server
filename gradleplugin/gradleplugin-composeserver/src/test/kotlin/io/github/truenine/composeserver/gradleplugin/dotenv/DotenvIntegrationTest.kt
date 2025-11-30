package io.github.truenine.composeserver.gradleplugin.dotenv

import io.github.truenine.composeserver.gradleplugin.Main
import io.github.truenine.composeserver.gradleplugin.entrance.ConfigEntrance
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import org.springframework.boot.gradle.tasks.run.BootRun
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test as JUnitTest

class DotenvIntegrationTest {

  @TempDir lateinit var tempDir: File

  private lateinit var project: Project

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().withProjectDir(tempDir).build()

    // Apply Spring Boot plugin
    project.pluginManager.apply("org.springframework.boot")

    // Apply our plugin
    project.pluginManager.apply(Main::class.java)
  }

  @JUnitTest
  fun `integrate_dotenv_functionality_with_gradle_plugin`() {
    // Create .env file
    val envFile = File(tempDir, ".env")
    envFile.writeText(
      """
      # Database configuration
      DB_HOST=localhost
      DB_PORT=5432
      DB_NAME="myapp_db"
      API_KEY='secret-key-123'

      # Application settings
      APP_ENV=development
      DEBUG=true
      """
        .trimIndent()
    )

    // Configure plugin
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.dotenv { config ->
      config.enabled = true
      config.filePath = ".env"
      config.verboseErrors = true
    }

    // Create test tasks
    val testTask = project.tasks.create("test", Test::class.java)
    val bootRunTask = project.tasks.create("bootRun", BootRun::class.java)
    val javaExecTask = project.tasks.create("runApp", JavaExec::class.java)
    val kotlinRunTask = project.tasks.create("run", JavaExec::class.java)

    // Trigger project evaluation
    (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

    // Verify environment variables injected into Test task
    assertEquals("localhost", testTask.environment["DB_HOST"])
    assertEquals("5432", testTask.environment["DB_PORT"])
    assertEquals("myapp_db", testTask.environment["DB_NAME"])
    assertEquals("secret-key-123", testTask.environment["API_KEY"])
    assertEquals("development", testTask.environment["APP_ENV"])
    assertEquals("true", testTask.environment["DEBUG"])

    // Verify environment variables injected into BootRun task
    assertEquals("localhost", bootRunTask.environment["DB_HOST"])
    assertEquals("5432", bootRunTask.environment["DB_PORT"])
    assertEquals("myapp_db", bootRunTask.environment["DB_NAME"])
    assertEquals("secret-key-123", bootRunTask.environment["API_KEY"])
    assertEquals("development", bootRunTask.environment["APP_ENV"])
    assertEquals("true", bootRunTask.environment["DEBUG"])

    // Verify environment variables injected into JavaExec task
    assertEquals("localhost", javaExecTask.environment["DB_HOST"])
    assertEquals("5432", javaExecTask.environment["DB_PORT"])
    assertEquals("myapp_db", javaExecTask.environment["DB_NAME"])
    assertEquals("secret-key-123", javaExecTask.environment["API_KEY"])
    assertEquals("development", javaExecTask.environment["APP_ENV"])
    assertEquals("true", javaExecTask.environment["DEBUG"])

    // Verify environment variables injected into Kotlin run task
    assertEquals("localhost", kotlinRunTask.environment["DB_HOST"])
    assertEquals("5432", kotlinRunTask.environment["DB_PORT"])
    assertEquals("myapp_db", kotlinRunTask.environment["DB_NAME"])
    assertEquals("secret-key-123", kotlinRunTask.environment["API_KEY"])
    assertEquals("development", kotlinRunTask.environment["APP_ENV"])
    assertEquals("true", kotlinRunTask.environment["DEBUG"])
  }

  @JUnitTest
  fun `handle_relative_path_correctly`() {
    // Create config directory and .env file
    val configDir = File(tempDir, "config")
    configDir.mkdirs()
    val envFile = File(configDir, ".env.local")
    envFile.writeText(
      """
      RELATIVE_PATH_TEST=success
      CONFIG_DIR_VAR=config_value
      """
        .trimIndent()
    )

    // Configure plugin to use relative path
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.dotenv { config ->
      config.enabled = true
      config.filePath = "config/.env.local"
    }

    // Create test task
    val testTask = project.tasks.create("test", Test::class.java)

    // Trigger project evaluation
    (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

    // Verify environment variables loaded correctly
    assertEquals("success", testTask.environment["RELATIVE_PATH_TEST"])
    assertEquals("config_value", testTask.environment["CONFIG_DIR_VAR"])
  }

  @JUnitTest
  fun `apply_filters_correctly_in_integration`() {
    // Create .env file
    val envFile = File(tempDir, ".env")
    envFile.writeText(
      """
      APP_NAME=myapp
      APP_VERSION=1.0.0
      DB_HOST=localhost
      SECRET_KEY=secret
      OTHER_VAR=other
      """
        .trimIndent()
    )

    // Configure plugin to use prefix filter and exclusions
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.dotenv { config ->
      config.enabled = true
      config.filePath = ".env"
      config.prefixFilter = "APP_"
      config.excludeKeys("SECRET_KEY")
    }

    // Create test task
    val testTask = project.tasks.create("test", Test::class.java)

    // Trigger project evaluation
    (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

    // Verify only variables with APP_ prefix are loaded and SECRET_KEY is excluded
    assertEquals("myapp", testTask.environment["APP_NAME"])
    assertEquals("1.0.0", testTask.environment["APP_VERSION"])
    assertTrue(!testTask.environment.containsKey("DB_HOST"))
    assertTrue(!testTask.environment.containsKey("SECRET_KEY"))
    assertTrue(!testTask.environment.containsKey("OTHER_VAR"))
  }

  @JUnitTest
  fun `handle_missing_file_gracefully_in_integration`() {
    // Configure plugin to point to a non-existent file
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.dotenv { config ->
      config.enabled = true
      config.filePath = "nonexistent.env"
      config.warnOnMissingFile = false
    }

    // Create test task
    val testTask = project.tasks.create("test", Test::class.java)

    // Trigger project evaluation (should not throw)
    (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

    // Verify no environment variables are injected (test variable should not exist)
    assertTrue(!testTask.environment.containsKey("SHOULD_NOT_LOAD"))
  }

  @JUnitTest
  fun `work_with_absolute_path`() {
    // Create temporary .env file
    val envFile = File.createTempFile("test", ".env")
    envFile.deleteOnExit()
    envFile.writeText(
      """
      ABSOLUTE_PATH_TEST=success
      TEMP_FILE_VAR=temp_value
      """
        .trimIndent()
    )

    // Configure plugin to use absolute path
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.dotenv { config ->
      config.enabled = true
      config.filePath = envFile.absolutePath
    }

    // Create test task
    val testTask = project.tasks.create("test", Test::class.java)

    // Trigger project evaluation
    (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

    // Verify environment variables loaded correctly
    assertEquals("success", testTask.environment["ABSOLUTE_PATH_TEST"])
    assertEquals("temp_value", testTask.environment["TEMP_FILE_VAR"])
  }

  @JUnitTest
  fun `not_load_when_disabled`() {
    // Create .env file
    val envFile = File(tempDir, ".env")
    envFile.writeText(
      """
      SHOULD_NOT_LOAD=value
      """
        .trimIndent()
    )

    // Configure plugin but keep it disabled
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.dotenv { config ->
      // Explicitly disable
      config.enabled = false
      config.filePath = ".env"
    }

    // Create test task
    val testTask = project.tasks.create("test", Test::class.java)

    // Trigger project evaluation
    (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

    // Verify no environment variables are injected
    assertTrue(!testTask.environment.containsKey("SHOULD_NOT_LOAD"))
  }
}
