plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.publish-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
Shared foundation library providing core utilities, exception handling, and common types for all modules.
Includes base configurations, extension functions, and fundamental building blocks used across the entire application.
"""
    .trimIndent()

dependencies {
  api(libs.com.fasterxml.jackson.core.jackson.annotations)
  api(libs.jakarta.annotation.jakarta.annotation.api)
  api(libs.jakarta.servlet.jakarta.servlet.api)
  api(libs.jakarta.inject.jakarta.inject.api)
  api(libs.jakarta.validation.jakarta.validation.api)
  api(libs.io.swagger.core.v3.swagger.annotations.jakarta)
  api(libs.org.slf4j.slf4j.api)
  api(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)

  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
  testImplementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
}
