plugins {
  id("buildlogic.kotlinspring-conventions")
}

dependencies {
  api(libs.org.jetbrains.kotlin.kotlin.test)
  api(libs.org.jetbrains.kotlin.kotlin.test.junit5)
  api(libs.io.mockk.mockk)
  api(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
  api(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  api(libs.org.junit.jupiter.junit.jupiter.params)

  api(libs.org.testcontainers.testcontainers)
  api(libs.org.testcontainers.postgresql)
  api(libs.org.testcontainers.junit.jupiter)
  api(libs.org.testcontainers.minio)

  implementation(libs.org.junit.jupiter.junit.jupiter.api)
  implementation(libs.org.junit.jupiter.junit.jupiter.engine)
  implementation(libs.org.junit.vintage.junit.vintage.engine)
  implementation(libs.org.slf4j.slf4j.api)

  // json
  api(libs.com.fasterxml.jackson.module.jackson.module.kotlin)
  api(libs.com.fasterxml.jackson.core.jackson.databind)
  runtimeOnly(libs.org.skyscreamer.jsonassert)

  // spring 测试支持
  runtimeOnly(libs.org.springframework.spring.test)
  runtimeOnly(libs.org.springframework.boot.spring.boot.test) {
    exclude("org.junit.jupiter")
    exclude("org.mockito", "mockito-core")
    exclude("org.mockito", "mockito-junit-jupiter")
    exclude("org.junit.platform")
  }

  implementation(libs.org.springframework.security.spring.security.test)
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
  runtimeOnly(libs.org.springframework.boot.spring.boot.starter.logging)

  testImplementation(libs.org.testcontainers.postgresql)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.jdbc)

  testImplementation(libs.io.minio.minio)
  testRuntimeOnly(libs.org.postgresql.postgresql)
  testImplementation(libs.org.testcontainers.junit.jupiter)
  testImplementation(libs.org.testcontainers.postgresql)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.data.redis)
  testImplementation(libs.org.testcontainers.testcontainers)
  testImplementation(libs.org.testcontainers.minio)
}
