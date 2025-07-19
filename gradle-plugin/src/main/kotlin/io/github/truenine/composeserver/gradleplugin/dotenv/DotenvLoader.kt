package io.github.truenine.composeserver.gradleplugin.dotenv

import io.github.truenine.composeserver.gradleplugin.consts.Constant
import io.github.truenine.composeserver.gradleplugin.wrap
import java.io.File
import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import org.springframework.boot.gradle.tasks.run.BootRun

/**
 * # Dotenv 环境变量加载器
 *
 * 负责从 .env 文件中解析环境变量并注入到 Gradle 任务的执行环境中
 *
 * @param project Gradle 项目实例
 * @param config Dotenv 配置
 * @author TrueNine
 * @since 2024-12-19
 */
class DotenvLoader(@Inject private val project: Project, @Inject private val config: DotenvConfig) {

  private val logger = project.logger
  private val loadedVariables = mutableMapOf<String, String>()

  init {
    if (config.isValid()) {
      loadEnvironmentVariables()
      injectIntoTasks()
    } else {
      logger.debug("Dotenv loader is disabled or configuration is invalid: ${config.getSummary()}")
    }
  }

  /** 从 .env 文件加载环境变量 */
  private fun loadEnvironmentVariables() {
    val dotenvFile = resolveDotenvFile()

    if (!dotenvFile.exists()) {
      if (config.warnOnMissingFile) {
        logger.warn("Dotenv file not found: ${dotenvFile.absolutePath}")
      }
      return
    }

    if (!dotenvFile.canRead()) {
      logger.error("Cannot read dotenv file: ${dotenvFile.absolutePath}")
      return
    }

    logger.info("Loading environment variables from: ${dotenvFile.absolutePath}")

    try {
      dotenvFile.readLines().forEachIndexed { lineNumber, line -> parseLine(line.trim(), lineNumber + 1) }

      logger.info("Successfully loaded ${loadedVariables.size} environment variables from dotenv file")
      if (logger.isDebugEnabled) {
        loadedVariables.forEach { (key, value) ->
          logger.debug(
            "Loaded env var: $key=${
              if (key.contains("PASSWORD", true) || key.contains("SECRET", true) || key.contains(
                  "KEY",
                  true,
                )
              ) "***" else value
            }"
          )
        }
      }
    } catch (e: Exception) {
      val message = "Failed to read dotenv file: ${dotenvFile.absolutePath}"
      if (config.verboseErrors) {
        logger.error(message, e)
      } else {
        logger.error("$message - ${e.message}")
      }
    }
  }

  /** 解析单行环境变量定义 */
  private fun parseLine(line: String, lineNumber: Int) {
    // 跳过空行和注释行
    if (line.isEmpty() || line.startsWith("#")) {
      return
    }

    val equalIndex = line.indexOf('=')
    if (equalIndex == -1) {
      if (config.verboseErrors) {
        logger.warn("Invalid line format at line $lineNumber: '$line' (missing '=')")
      }
      return
    }

    val key = line.substring(0, equalIndex).trim()
    val rawValue = line.substring(equalIndex + 1).trim()

    if (key.isEmpty()) {
      if (config.verboseErrors) {
        logger.warn("Empty key at line $lineNumber: '$line'")
      }
      return
    }

    val value = parseValue(rawValue)

    // 检查是否忽略空值
    if (config.ignoreEmptyValues && value.isEmpty()) {
      logger.debug("Ignoring empty value for key: $key")
      return
    }

    // 应用前缀过滤器
    config.prefixFilter?.let { prefix ->
      if (!key.startsWith(prefix)) {
        logger.debug("Skipping key '$key' (doesn't match prefix filter: '$prefix')")
        return
      }
    }

    // 检查排除列表
    if (config.excludeKeys.contains(key)) {
      logger.debug("Skipping excluded key: $key")
      return
    }

    // 检查包含列表
    if (config.includeKeys.isNotEmpty() && !config.includeKeys.contains(key)) {
      logger.debug("Skipping key '$key' (not in include list)")
      return
    }

    // 检查是否覆盖已存在的环境变量
    if (!config.overrideExisting && System.getenv(key) != null) {
      logger.debug("Skipping key '$key' (already exists in environment and override is disabled)")
      return
    }

    loadedVariables[key] = value
  }

  /** 解析环境变量值，处理引号和转义字符 */
  private fun parseValue(rawValue: String): String {
    if (rawValue.isEmpty()) return rawValue

    // 处理双引号
    if (rawValue.startsWith("\"") && rawValue.endsWith("\"") && rawValue.length >= 2) {
      return rawValue
        .substring(1, rawValue.length - 1)
        .replace("\\\"", "\"")
        .replace("\\n", "\n")
        .replace("\\r", "\r")
        .replace("\\t", "\t")
        .replace("\\\\", "\\")
    }

    // 处理单引号
    if (rawValue.startsWith("'") && rawValue.endsWith("'") && rawValue.length >= 2) {
      return rawValue.substring(1, rawValue.length - 1)
    }

    return rawValue
  }

  /** 解析 dotenv 文件路径 */
  private fun resolveDotenvFile(): File {
    val path = config.filePath

    return if (File(path).isAbsolute) {
      File(path)
    } else {
      project.rootProject.file(path)
    }
  }

  /** 将环境变量注入到 Gradle 任务中 */
  private fun injectIntoTasks() {
    if (loadedVariables.isEmpty()) {
      logger.debug("No environment variables to inject")
      return
    }

    project.wrap {
      // 注入到所有测试任务
      tasks.withType(Test::class.java).configureEach { testTask ->
        loadedVariables.forEach { (key, value) -> testTask.environment(key, value) }
        logger.debug("Injected ${loadedVariables.size} environment variables into test task: ${testTask.name}")
      }

      // 注入到所有 JavaExec 任务（包括自定义的 Java 执行任务）
      tasks.withType(JavaExec::class.java).configureEach { javaExecTask ->
        loadedVariables.forEach { (key, value) -> javaExecTask.environment(key, value) }
        logger.debug("Injected ${loadedVariables.size} environment variables into JavaExec task: ${javaExecTask.name}")
      }

      // 注入到 Spring Boot 运行任务
      tasks.withType(BootRun::class.java).configureEach { bootRunTask ->
        loadedVariables.forEach { (key, value) -> bootRunTask.environment(key, value) }
        logger.debug("Injected ${loadedVariables.size} environment variables into bootRun task: ${bootRunTask.name}")
      }

      // 注入到 Kotlin 运行任务（如果存在）
      afterEvaluate {
        // 处理 Kotlin 应用插件的 run 任务
        tasks.findByName("run")?.let { runTask ->
          if (runTask is JavaExec) {
            loadedVariables.forEach { (key, value) -> runTask.environment(key, value) }
            logger.debug("Injected ${loadedVariables.size} environment variables into Kotlin run task")
          }
        }

        // 注入到 Quarkus 开发任务（如果存在）
        tasks.findByName("quarkusDev")?.let { quarkusDevTask ->
          try {
            // Quarkus 任务可能有不同的环境变量设置方式
            val currentEnv = quarkusDevTask.property("environment") as? Map<String, String> ?: emptyMap()
            val newEnv = currentEnv + loadedVariables
            quarkusDevTask.setProperty("environment", newEnv)
            logger.debug("Injected ${loadedVariables.size} environment variables into quarkusDev task")
          } catch (e: Exception) {
            logger.debug("Could not inject environment variables into quarkusDev task: ${e.message}")
          }
        }

        // 注入到 Micronaut 开发任务（如果存在）
        tasks.findByName("mn:run")?.let { micronautRunTask ->
          if (micronautRunTask is JavaExec) {
            loadedVariables.forEach { (key, value) -> micronautRunTask.environment(key, value) }
            logger.debug("Injected ${loadedVariables.size} environment variables into Micronaut run task")
          }
        }

        // 为其他可能需要环境变量的任务创建通用注入机制
        tasks.configureEach { task ->
          // 跳过已经处理过的任务类型
          if (task !is Test && task !is JavaExec && task.name !in setOf("quarkusDev", "mn:run")) {

            // 尝试为有 environment 属性的任务注入环境变量
            if (task.hasProperty("environment")) {
              try {
                val currentEnv = task.property("environment") as? Map<String, String> ?: emptyMap()
                val newEnv = currentEnv + loadedVariables
                task.setProperty("environment", newEnv)
                logger.debug("Injected ${loadedVariables.size} environment variables into task: ${task.name}")
              } catch (e: Exception) {
                logger.debug("Could not inject environment variables into task ${task.name}: ${e.message}")
              }
            }
          }
        }
      }
    }
  }

  /** 获取已加载的环境变量 */
  fun getLoadedVariables(): Map<String, String> = loadedVariables.toMap()

  companion object {
    const val TASK_GROUP = Constant.TASK_GROUP
  }
}
