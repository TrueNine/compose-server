package io.github.truenine.composeserver.gradleplugin.dotenv

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import java.io.File

/**
 * # Dotenv Gradle Plugin
 *
 * Main entry point for the Compose Server Dotenv Gradle plugin.
 * This plugin provides support for loading environment variables from .env files.
 *
 * @author TrueNine
 * @since 2025-10-30
 */
class Main : Plugin<Project> {

  override fun apply(project: Project) {
    val logger = project.logger
    val envFile = project.rootProject.file(".env")

    val variables = loadEnvFile(envFile, logger)

    if (variables.isEmpty()) {
      return
    }

    injectIntoTests(project, variables)
    injectIntoJavaExec(project, variables)

    logger.info(
      "Loaded ${variables.size} environment variable(s) from ${envFile.absolutePath} for project ${project.name}"
    )
  }

  private fun loadEnvFile(envFile: File, logger: org.gradle.api.logging.Logger): Map<String, String> {
    if (!envFile.exists()) {
      logger.warn(".env file not found at: ${envFile.absolutePath}")
      return emptyMap()
    }

    if (!envFile.canRead()) {
      logger.error("Cannot read .env file at: ${envFile.absolutePath}")
      return emptyMap()
    }

    val variables = linkedMapOf<String, String>()

    envFile.readLines().forEachIndexed { index, originalLine ->
      val line = originalLine.trim()

      if (line.isEmpty() || line.startsWith("#")) {
        return@forEachIndexed
      }

      val parts = line.split('=', limit = 2)
      if (parts.size != 2) {
        logger.debug("Skipping invalid dotenv line ${index + 1}: '$originalLine'")
        return@forEachIndexed
      }

      val key = parts[0].trim()
      if (key.isEmpty()) {
        logger.debug("Skipping dotenv line with empty key at ${index + 1}: '$originalLine'")
        return@forEachIndexed
      }

      val value = parts[1].trim().removeSurrounding("\"").removeSurrounding("'")

      if (value.isEmpty()) {
        logger.debug("Skipping dotenv line with empty value for key '$key' at ${index + 1}")
        return@forEachIndexed
      }

      variables[key] = value
      logger.debug("Loaded dotenv variable '$key'")
    }

    return variables
  }

  private fun injectIntoTests(project: Project, variables: Map<String, String>) {
    project.tasks.withType(Test::class.java).configureEach { task ->
      variables.forEach { (key, value) -> task.environment(key, value) }
    }
  }

  private fun injectIntoJavaExec(project: Project, variables: Map<String, String>) {
    project.tasks.withType(JavaExec::class.java).configureEach { task ->
      variables.forEach { (key, value) -> task.environment(key, value) }
    }
  }
}

