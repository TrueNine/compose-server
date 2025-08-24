plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
Core testing toolkit providing foundational components for test development.
Includes test utilities, Spring Boot test auto-configuration, and unified testing extensions.
"""
    .trimIndent()

dependencies {
  api(libs.org.jetbrains.kotlin.kotlin.test)
  api(libs.org.jetbrains.kotlin.kotlin.test.junit5)
  api(libs.io.mockk.mockk)
  api(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
  api(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  api(libs.org.junit.jupiter.junit.jupiter.params)

  implementation(libs.org.junit.jupiter.junit.jupiter.api)
  implementation(libs.org.junit.jupiter.junit.jupiter.engine)
  implementation(libs.org.junit.vintage.junit.vintage.engine)
  implementation(libs.org.slf4j.slf4j.api)

  // json
  api(libs.com.fasterxml.jackson.module.jackson.module.kotlin)
  api(libs.com.fasterxml.jackson.core.jackson.databind)
  runtimeOnly(libs.org.skyscreamer.jsonassert)

  // spring 测试支持
  implementation(libs.org.springframework.spring.test)
  implementation(libs.org.springframework.spring.web)
  runtimeOnly(libs.org.springframework.boot.spring.boot.test) {
    exclude("org.junit.jupiter")
    exclude("org.mockito", "mockito-core")
    exclude("org.mockito", "mockito-junit-jupiter")
    exclude("org.junit.platform")
  }

  // Spring Security 核心依赖
  implementation(libs.org.springframework.boot.spring.boot.test.autoconfigure)

  api(libs.org.springframework.boot.spring.boot.starter.test) {
    exclude("org.junit.jupiter")
    exclude("org.junit.platform")
    exclude("org.mockito", "mockito-core")
    exclude("org.mockito", "mockito-junit-jupiter")
  }

  // spring batch
  api(libs.org.springframework.batch.spring.batch.test)

  // 日志自动配置
  runtimeOnly(libs.ch.qos.logback.logback.classic)
}
