plugins {
  id("buildlogic.kotlin-spring-boot-conventions")
  id("buildlogic.maven-publish-conventions")
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
  api(libs.org.jspecify.jspecify)

  testImplementation(projects.testtoolkit.testtoolkitSpringmvc)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
  testImplementation(libs.org.springframework.boot.spring.boot.test.autoconfigure)
  testImplementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  testImplementation(libs.tools.jackson.core.jackson.databind)
}

// Disable KAPT for test sources to avoid Jackson 3.x annotation processing issues
afterEvaluate {
  tasks.findByName("kaptTestKotlin")?.enabled = false
  tasks.findByName("kaptGenerateStubsTestKotlin")?.enabled = false
}
