plugins {
  id("buildlogic.kotlinspring-conventions")
}

description = """
Model Context Protocol (MCP) shared components for AI integration and context management.
Provides common interfaces and utilities for AI model interactions and context handling.
""".trimIndent()

dependencies {
  implementation(projects.shared)
  testImplementation(projects.testtoolkit)
}
