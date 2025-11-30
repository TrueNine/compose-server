plugins {
  id("buildlogic.kotlin-spring-boot-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  XXL-JOB distributed task scheduling framework integration for enterprise job management.
  Provides distributed task execution, scheduling, and monitoring capabilities with fault tolerance.
  """
    .trimIndent()

dependencies {
  api(libs.com.xuxueli.xxl.job.core)
  api(projects.shared)
}
