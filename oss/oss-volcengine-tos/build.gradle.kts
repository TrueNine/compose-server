plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
  id("buildlogic.loadenv-conventions")
}

description =
  """
Volcengine TOS (Tinder Object Storage) integration for enterprise-grade cloud storage.
Provides comprehensive TOS operations including object management, lifecycle policies, and security configurations.
"""
    .trimIndent()

dependencies {
  api(projects.oss.ossShared)
  api(projects.shared)
  implementation(libs.com.volcengine.ve.tos.java.sdk)

  testImplementation(projects.testtoolkit.testtoolkitShared)
}
