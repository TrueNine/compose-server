package io.github.truenine.composeserver.gradleplugin.dotenv

import io.github.truenine.composeserver.gradleplugin.consts.Constant
import io.github.truenine.composeserver.gradleplugin.wrap
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import org.springframework.boot.gradle.tasks.run.BootRun
import java.io.File
import javax.inject.Inject

/**
 * # Dotenv environment variable loader
 *
 * Responsible for parsing environment variables from .env files and injecting them into the execution environment of Gradle tasks.
 *
 * @param project Gradle project instance
 * @param config Dotenv configuration
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

  /** Load environment variables from the .env file */
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

  /** Parse a single environment variable definition line */
  private fun parseLine(line: String, lineNumber: Int) {
    // Skip empty lines and commented lines
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

    // Check whether to ignore empty values
    if (config.ignoreEmptyValues && value.isEmpty()) {
      logger.debug("Ignoring empty value for key: $key")
      return
    }

    // Apply prefix filter
    config.prefixFilter?.let { prefix ->
      if (!key.startsWith(prefix)) {
        logger.debug("Skipping key '$key' (doesn't match prefix filter: '$prefix')")
        return
      }
    }

    // Check exclude list
    if (config.excludeKeys.contains(key)) {
      logger.debug("Skipping excluded key: $key")
      return
    }

    // Check include list
    if (config.includeKeys.isNotEmpty() && !config.includeKeys.contains(key)) {
      logger.debug("Skipping key '$key' (not in include list)")
      return
    }

    // Check whether to override existing environment variables
    if (!config.overrideExisting && System.getenv(key) != null) {
      logger.debug("Skipping key '$key' (already exists in environment and override is disabled)")
      return
    }

    loadedVariables[key] = value
  }

  /** Parse environment variable value, handling quotes and escape characters */
  private fun parseValue(rawValue: String): String {
    if (rawValue.isEmpty()) return rawValue

    // Handle double quotes
    if (rawValue.startsWith("\"") && rawValue.endsWith("\"") && rawValue.length >= 2) {
      return rawValue
        .substring(1, rawValue.length - 1)
        .replace("\\\"", "\"")
        .replace("\\n", "\n")
        .replace("\\r", "\r")
        .replace("\\t", "\t")
        .replace("\\\\", "\\")
    }

    // Handle single quotes
    if (rawValue.startsWith("'") && rawValue.endsWith("'") && rawValue.length >= 2) {
      return rawValue.substring(1, rawValue.length - 1)
    }

    return rawValue
  }

  /** Resolve the dotenv file path */
  private fun resolveDotenvFile(): File {
    val path = config.filePath

    return if (File(path).isAbsolute) {
      File(path)
    } else {
      project.rootProject.file(path)
    }
  }

  /** Inject environment variables into Gradle tasks */
  private fun injectIntoTasks() {
    if (loadedVariables.isEmpty()) {
      logger.debug("No environment variables to inject")
      return
    }

    project.wrap {
      // Inject into all test tasks
      tasks.withType(Test::class.java).configureEach { testTask ->
        loadedVariables.forEach { (key, value) -> testTask.environment(key, value) }
        logger.debug("Injected ${loadedVariables.size} environment variables into test task: ${testTask.name}")
      }

      // Inject into all JavaExec tasks (including custom Java execution tasks)
      tasks.withType(JavaExec::class.java).configureEach { javaExecTask ->
        loadedVariables.forEach { (key, value) -> javaExecTask.environment(key, value) }
        logger.debug("Injected ${loadedVariables.size} environment variables into JavaExec task: ${javaExecTask.name}")
      }

      // Inject into Spring Boot run tasks
      tasks.withType(BootRun::class.java).configureEach { bootRunTask ->
        loadedVariables.forEach { (key, value) -> bootRunTask.environment(key, value) }
        logger.debug("Injected ${loadedVariables.size} environment variables into bootRun task: ${bootRunTask.name}")
      }

      // Inject into Kotlin run tasks (if present)
      afterEvaluate {
        // Handle run task of the Kotlin application plugin
        tasks.findByName("run")?.let { runTask ->
          if (runTask is JavaExec) {
            loadedVariables.forEach { (key, value) -> runTask.environment(key, value) }
            logger.debug("Injected ${loadedVariables.size} environment variables into Kotlin run task")
          }
        }

        // Inject into Quarkus dev task (if present)
        tasks.findByName("quarkusDev")?.let { quarkusDevTask ->
          try {
            // Quarkus tasks may have different ways to configure environment variables
            val currentEnv = quarkusDevTask.property("environment") as? Map<String, String> ?: emptyMap()
            val newEnv = currentEnv + loadedVariables
            quarkusDevTask.setProperty("environment", newEnv)
            logger.debug("Injected ${loadedVariables.size} environment variables into quarkusDev task")
          } catch (e: Exception) {
            logger.debug("Could not inject environment variables into quarkusDev task: ${e.message}")
          }
        }

        // Inject into Micronaut dev task (if present)
        tasks.findByName("mn:run")?.let { micronautRunTask ->
          if (micronautRunTask is JavaExec) {
            loadedVariables.forEach { (key, value) -> micronautRunTask.environment(key, value) }
            logger.debug("Injected ${loadedVariables.size} environment variables into Micronaut run task")
          }
        }

        // Create a generic injection mechanism for other tasks that may require environment variables
        tasks.configureEach { task ->
          // Skip task types that have already been processed
          if (task !is Test && task !is JavaExec && task.name !in setOf("quarkusDev", "mn:run")) {

            // Try to inject environment variables into tasks that have an environment property
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

  /** Get loaded environment variables */
  fun getLoadedVariables(): Map<String, String> = loadedVariables.toMap()

  companion object {
    const val TASK_GROUP = Constant.TASK_GROUP
  }
}
