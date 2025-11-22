plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Spring MVC testing toolkit extension providing web layer testing utilities.
  Includes specialized test support for controllers, REST APIs, security, and web integration testing scenarios.
  """
    .trimIndent()

dependencies {
  api(projects.testtoolkit.testtoolkitShared)

  api(libs.org.jetbrains.kotlin.kotlin.test)
  api(libs.org.jetbrains.kotlin.kotlin.test.junit5)
  api(libs.io.mockk.mockk)
  api(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
  api(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  api(libs.org.junit.jupiter.junit.jupiter.params)

  api(libs.org.testcontainers.testcontainers)
  api(libs.org.testcontainers.postgresql)
  api(libs.org.testcontainers.mysql)
  api(libs.org.testcontainers.junit.jupiter)
  api(libs.org.testcontainers.minio)
  api(libs.io.minio.minio)

  implementation(libs.org.junit.jupiter.junit.jupiter.api)
  implementation(libs.org.junit.jupiter.junit.jupiter.engine)
  implementation(libs.org.junit.vintage.junit.vintage.engine)
  implementation(libs.org.slf4j.slf4j.api)

  // json
  api(libs.com.fasterxml.jackson.module.jackson.module.kotlin)
  api(libs.com.fasterxml.jackson.core.jackson.databind)
  runtimeOnly(libs.org.skyscreamer.jsonassert)

  // Spring test support
  implementation(libs.org.springframework.spring.test)
  runtimeOnly(libs.org.springframework.boot.spring.boot.test) {
    exclude("org.junit.jupiter")
    exclude("org.mockito", "mockito-core")
    exclude("org.mockito", "mockito-junit-jupiter")
    exclude("org.junit.platform")
  }

  // Spring Security core dependencies
  implementation(libs.org.springframework.boot.spring.boot.test.autoconfigure)

  api(libs.org.springframework.boot.spring.boot.starter.test) {
    exclude("org.junit.jupiter")
    exclude("org.junit.platform")
    exclude("org.mockito", "mockito-core")
    exclude("org.mockito", "mockito-junit-jupiter")
  }

  // spring batch
  api(libs.org.springframework.batch.spring.batch.test)

  // Logging auto-configuration
  runtimeOnly(libs.ch.qos.logback.logback.classic)

  testImplementation(libs.org.testcontainers.postgresql)
  testImplementation(libs.org.testcontainers.mysql)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.jdbc)

  testRuntimeOnly(libs.org.postgresql.postgresql)
  testRuntimeOnly(libs.com.mysql.mysql.connector.j)
  testImplementation(libs.org.testcontainers.junit.jupiter)
  testImplementation(libs.org.testcontainers.postgresql)
  testImplementation(libs.org.testcontainers.mysql)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.data.redis)
  testImplementation(libs.org.testcontainers.testcontainers)
  testImplementation(libs.org.testcontainers.minio)
}
