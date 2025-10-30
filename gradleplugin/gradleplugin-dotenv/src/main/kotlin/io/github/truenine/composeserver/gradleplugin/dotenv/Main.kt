package io.github.truenine.composeserver.gradleplugin.dotenv

import org.gradle.api.Plugin
import org.gradle.api.Project

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
    // TODO: Implement dotenv plugin logic
    project.logger.info("Compose Server Dotenv Plugin applied to project: ${project.name}")
  }
}

