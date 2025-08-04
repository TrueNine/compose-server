plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
Shared abstractions and interfaces for object storage operations across different cloud providers.
Provides unified API for file upload, download, and management operations regardless of the underlying storage service.
"""
    .trimIndent()

dependencies {
  implementation(libs.com.fasterxml.jackson.core.jackson.databind)
  api(projects.shared)
  api(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)

  testImplementation(projects.testtoolkit)
}
