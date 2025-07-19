package io.github.truenine.composeserver.gradleplugin.dotenv

import io.github.truenine.composeserver.gradleplugin.Main
import io.github.truenine.composeserver.gradleplugin.entrance.ConfigEntrance
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test as JUnitTest
import org.junit.jupiter.api.io.TempDir
import org.springframework.boot.gradle.tasks.run.BootRun

class DotenvIntegrationTest {

  @TempDir lateinit var tempDir: File

  private lateinit var project: Project

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().withProjectDir(tempDir).build()

    // 应用Spring Boot插件
    project.pluginManager.apply("org.springframework.boot")

    // 应用我们的插件
    project.pluginManager.apply(Main::class.java)
  }

  @JUnitTest
  fun `should integrate dotenv functionality with gradle plugin`() {
    // 创建.env文件
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

    // 配置插件
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.dotenv { config ->
      config.enabled = true
      config.filePath = ".env"
      config.verboseErrors = true
    }

    // 创建测试任务
    val testTask = project.tasks.create("test", Test::class.java)
    val bootRunTask = project.tasks.create("bootRun", BootRun::class.java)
    val javaExecTask = project.tasks.create("runApp", JavaExec::class.java)
    val kotlinRunTask = project.tasks.create("run", JavaExec::class.java)

    // 触发项目评估
    (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

    // 验证环境变量已注入到测试任务
    assertEquals("localhost", testTask.environment["DB_HOST"])
    assertEquals("5432", testTask.environment["DB_PORT"])
    assertEquals("myapp_db", testTask.environment["DB_NAME"])
    assertEquals("secret-key-123", testTask.environment["API_KEY"])
    assertEquals("development", testTask.environment["APP_ENV"])
    assertEquals("true", testTask.environment["DEBUG"])

    // 验证环境变量已注入到bootRun任务
    assertEquals("localhost", bootRunTask.environment["DB_HOST"])
    assertEquals("5432", bootRunTask.environment["DB_PORT"])
    assertEquals("myapp_db", bootRunTask.environment["DB_NAME"])
    assertEquals("secret-key-123", bootRunTask.environment["API_KEY"])
    assertEquals("development", bootRunTask.environment["APP_ENV"])
    assertEquals("true", bootRunTask.environment["DEBUG"])

    // 验证环境变量已注入到JavaExec任务
    assertEquals("localhost", javaExecTask.environment["DB_HOST"])
    assertEquals("5432", javaExecTask.environment["DB_PORT"])
    assertEquals("myapp_db", javaExecTask.environment["DB_NAME"])
    assertEquals("secret-key-123", javaExecTask.environment["API_KEY"])
    assertEquals("development", javaExecTask.environment["APP_ENV"])
    assertEquals("true", javaExecTask.environment["DEBUG"])

    // 验证环境变量已注入到Kotlin run任务
    assertEquals("localhost", kotlinRunTask.environment["DB_HOST"])
    assertEquals("5432", kotlinRunTask.environment["DB_PORT"])
    assertEquals("myapp_db", kotlinRunTask.environment["DB_NAME"])
    assertEquals("secret-key-123", kotlinRunTask.environment["API_KEY"])
    assertEquals("development", kotlinRunTask.environment["APP_ENV"])
    assertEquals("true", kotlinRunTask.environment["DEBUG"])
  }

  @JUnitTest
  fun `should handle relative path correctly`() {
    // 创建config目录和.env文件
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

    // 配置插件使用相对路径
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.dotenv { config ->
      config.enabled = true
      config.filePath = "config/.env.local"
    }

    // 创建测试任务
    val testTask = project.tasks.create("test", Test::class.java)

    // 触发项目评估
    (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

    // 验证环境变量已正确加载
    assertEquals("success", testTask.environment["RELATIVE_PATH_TEST"])
    assertEquals("config_value", testTask.environment["CONFIG_DIR_VAR"])
  }

  @JUnitTest
  fun `should apply filters correctly in integration`() {
    // 创建.env文件
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

    // 配置插件使用前缀过滤和排除
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.dotenv { config ->
      config.enabled = true
      config.filePath = ".env"
      config.prefixFilter = "APP_"
      config.excludeKeys("SECRET_KEY")
    }

    // 创建测试任务
    val testTask = project.tasks.create("test", Test::class.java)

    // 触发项目评估
    (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

    // 验证只有APP_前缀的变量被加载，且SECRET_KEY被排除
    assertEquals("myapp", testTask.environment["APP_NAME"])
    assertEquals("1.0.0", testTask.environment["APP_VERSION"])
    assertTrue(!testTask.environment.containsKey("DB_HOST"))
    assertTrue(!testTask.environment.containsKey("SECRET_KEY"))
    assertTrue(!testTask.environment.containsKey("OTHER_VAR"))
  }

  @JUnitTest
  fun `should handle missing file gracefully in integration`() {
    // 配置插件指向不存在的文件
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.dotenv { config ->
      config.enabled = true
      config.filePath = "nonexistent.env"
      config.warnOnMissingFile = false
    }

    // 创建测试任务
    val testTask = project.tasks.create("test", Test::class.java)

    // 触发项目评估（不应该抛出异常）
    (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

    // 验证没有环境变量被注入（检查我们的测试变量不存在）
    assertTrue(!testTask.environment.containsKey("SHOULD_NOT_LOAD"))
  }

  @JUnitTest
  fun `should work with absolute path`() {
    // 创建临时.env文件
    val envFile = File.createTempFile("test", ".env")
    envFile.deleteOnExit()
    envFile.writeText(
      """
      ABSOLUTE_PATH_TEST=success
      TEMP_FILE_VAR=temp_value
    """
        .trimIndent()
    )

    // 配置插件使用绝对路径
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.dotenv { config ->
      config.enabled = true
      config.filePath = envFile.absolutePath
    }

    // 创建测试任务
    val testTask = project.tasks.create("test", Test::class.java)

    // 触发项目评估
    (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

    // 验证环境变量已正确加载
    assertEquals("success", testTask.environment["ABSOLUTE_PATH_TEST"])
    assertEquals("temp_value", testTask.environment["TEMP_FILE_VAR"])
  }

  @JUnitTest
  fun `should not load when disabled`() {
    // 创建.env文件
    val envFile = File(tempDir, ".env")
    envFile.writeText(
      """
      SHOULD_NOT_LOAD=value
    """
        .trimIndent()
    )

    // 配置插件但保持禁用状态
    val configEntrance = project.extensions.getByType(ConfigEntrance::class.java)
    configEntrance.dotenv { config ->
      config.enabled = false // 明确禁用
      config.filePath = ".env"
    }

    // 创建测试任务
    val testTask = project.tasks.create("test", Test::class.java)

    // 触发项目评估
    (project as org.gradle.api.internal.project.ProjectInternal).evaluate()

    // 验证没有环境变量被注入
    assertTrue(!testTask.environment.containsKey("SHOULD_NOT_LOAD"))
  }
}
