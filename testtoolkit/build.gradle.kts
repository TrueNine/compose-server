plugins { `kotlin-convention` }

version = libs.versions.compose.build.get()

dependencies {
  api(libs.org.jetbrains.kotlin.kotlin.test)
  api(libs.org.jetbrains.kotlin.kotlin.test.junit5)
  api(libs.io.mockk.mockk)

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
    exclude("org.junit.platform")
  }

  runtimeOnly(libs.org.springframework.boot.spring.boot.test.autoconfigure)
  implementation(libs.org.springframework.security.spring.security.test)
  implementation(libs.org.springframework.boot.spring.boot.test.autoconfigure)

  api(libs.org.springframework.boot.spring.boot.starter.test) {
    exclude("org.junit.jupiter")
    exclude("org.junit.platform")
  }

  // spring batch
  api(libs.org.springframework.batch.spring.batch.test)

  // 日志自动配置
  runtimeOnly(libs.org.springframework.boot.spring.boot.starter.logging)

  // 测试用数据库
  runtimeOnly(libs.com.h2database.h2)
}
