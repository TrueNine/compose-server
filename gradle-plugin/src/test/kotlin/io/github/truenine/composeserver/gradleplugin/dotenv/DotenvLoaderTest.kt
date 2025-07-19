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

    // 应用Spring Boot插件以获得BootRun任务
    project.pluginManager.apply("org.springframework.boot")

    config = DotenvConfig()
  }

  @JUnitTest
  fun `should not load when config is invalid`() {
    config.enabled = false

    val loader = DotenvLoader(project, config)

    assertTrue(loader.getLoadedVariables().isEmpty())
  }

  @JUnitTest
  fun `should handle missing dotenv file gracefully`() {
    config.enabled = true
    config.filePath = "nonexistent.env"
    config.warnOnMissingFile = false

    val loader = DotenvLoader(project, config)

    assertTrue(loader.getLoadedVariables().isEmpty())
  }

  @JUnitTest
  fun `should parse simple key-value pairs`() {
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
  fun `should handle quoted values correctly`() {
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
  fun `should skip comments and empty lines`() {
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
  fun `should apply prefix filter`() {
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
  fun `should exclude specified keys`() {
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
  fun `should include only specified keys`() {
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
  fun `should ignore empty values when configured`() {
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
  fun `should inject variables into test tasks`() {
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

    // 创建测试任务
    val testTask = project.tasks.create("test", Test::class.java)

    val loader = DotenvLoader(project, config)

    // 验证环境变量已注入
    assertEquals("test_value", testTask.environment["TEST_VAR"])
    assertEquals("localhost", testTask.environment["DB_HOST"])
  }

  @JUnitTest
  fun `should inject variables into bootRun tasks`() {
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

    // 创建BootRun任务
    val bootRunTask = project.tasks.create("bootRun", BootRun::class.java)

    val loader = DotenvLoader(project, config)

    // 验证环境变量已注入
    assertEquals("dev", bootRunTask.environment["SPRING_PROFILE"])
    assertEquals("8080", bootRunTask.environment["SERVER_PORT"])
  }

  @JUnitTest
  fun `should inject variables into JavaExec tasks`() {
    val envFile =
      createTempEnvFile(
        """
      JAVA_OPTS=-Xmx1g
      APP_ENV=production
      DATABASE_URL=jdbc:postgresql://localhost:5432/mydb
    """
          .trimIndent()
      )

    config.enabled = true
    config.filePath = envFile.absolutePath

    // 创建JavaExec任务
    val javaExecTask = project.tasks.create("runApp", JavaExec::class.java)

    val loader = DotenvLoader(project, config)

    // 验证环境变量已注入
    assertEquals("-Xmx1g", javaExecTask.environment["JAVA_OPTS"])
    assertEquals("production", javaExecTask.environment["APP_ENV"])
    assertEquals("jdbc:postgresql://localhost:5432/mydb", javaExecTask.environment["DATABASE_URL"])
  }

  @JUnitTest
  fun `should inject variables into Kotlin run task`() {
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

    // 创建Kotlin应用的run任务（JavaExec类型）
    val runTask = project.tasks.create("run", JavaExec::class.java)

    val loader = DotenvLoader(project, config)

    // 验证环境变量已注入
    assertEquals("development", runTask.environment["KOTLIN_ENV"])
    assertEquals("true", runTask.environment["DEBUG_MODE"])
  }

  private fun createTempEnvFile(content: String): File {
    val envFile = File(tempDir, ".env")
    envFile.writeText(content)
    return envFile
  }
}
