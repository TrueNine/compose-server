plugins { `kotlin-convention` }

version = libs.versions.compose.build.get()

java {
  val jv = JavaVersion.VERSION_17
  sourceCompatibility = jv
  targetCompatibility = jv
  toolchain { languageVersion.set(JavaLanguageVersion.of(jv.ordinal + 1)) }
  withSourcesJar()
}

dependencies {
  api(libs.org.jetbrains.kotlin.kotlin.test)
  api(libs.org.jetbrains.kotlin.kotlin.test.junit5)
  // mockk
  api(libs.io.mockk.mockk)
  // kotlin 协程测试
  api(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
  api(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  // junit5 参数化测试扩展包
  api(libs.org.junit.jupiter.junit.jupiter.params)

  // Testcontainers
  api(libs.org.testcontainers.testcontainers)
  api(libs.org.testcontainers.postgresql)
  api(libs.org.testcontainers.junit.jupiter)

  implementation(libs.org.junit.jupiter.junit.jupiter.api) // 覆盖依赖
  implementation(libs.org.junit.jupiter.junit.jupiter.engine) // kotlin
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

  runtimeOnly(libs.org.springframework.boot.spring.boot.test.autoconfigure)
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

  // 测试用数据库
  runtimeOnly(libs.com.h2database.h2)
  testImplementation(libs.org.testcontainers.postgresql)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.jdbc)
  testRuntimeOnly(libs.org.postgresql.postgresql)

  // Redis 支持
  testImplementation(libs.org.springframework.boot.spring.boot.starter.data.redis)

  // MinIO 支持
  testImplementation(libs.io.minio.minio)

  // 测试容器支持
  testImplementation(libs.org.testcontainers.junit.jupiter)
  testImplementation(libs.org.testcontainers.postgresql)
  testImplementation(libs.io.minio.minio)
  testImplementation(libs.org.testcontainers.testcontainers)
}
