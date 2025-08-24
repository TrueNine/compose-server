plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
AI shared components for artificial intelligence integration and context management.
Provides common interfaces and utilities for AI model interactions and intelligent processing.
"""
    .trimIndent()

dependencies {
  implementation(projects.shared)

  testImplementation(projects.testtoolkit.testtoolkitShared)
}
