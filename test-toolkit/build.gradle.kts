plugins {
  `kotlin-convention`
}

version = libs.versions.composeTestToolkit.get()

dependencies {
  api(libs.org.jetbrains.kotlin.kotlinTest)
  api(libs.org.jetbrains.kotlin.kotlinTestJunit5) {
    exclude("org.junit.platform")
  }
  api(libs.io.mockk.mockk)

  implementation(libs.org.junit.jupiter.junitJupiterApi) // 覆盖依赖
  implementation(libs.org.junit.jupiter.junitJupiterEngine) // kotlin
  implementation(libs.org.slf4j.slf4jApi)

  // json
  api(libs.com.fasterxml.jackson.module.jacksonModuleKotlin)
  api(libs.com.fasterxml.jackson.core.jacksonDatabind)
  runtimeOnly(libs.org.skyscreamer.jsonassert)

  // spring 测试支持
  runtimeOnly(libs.org.springframework.springTest)
  runtimeOnly(libs.org.springframework.boot.springBootTest) {
    exclude("org.junit.jupiter")
    exclude("org.junit.platform")
  }

  runtimeOnly(libs.org.springframework.boot.springBootTestAutoconfigure)
  implementation(libs.org.springframework.security.springSecurityTest)
  implementation(libs.org.springframework.boot.springBootTestAutoconfigure)

  api(libs.org.springframework.boot.springBootStarterTest) {
    exclude("org.junit.jupiter")
    exclude("org.junit.platform")
  }

  // spring batch
  api(libs.org.springframework.batch.springBatchTest)

  // 日志自动配置
  runtimeOnly(libs.org.springframework.boot.springBootStarterLogging)

  // 测试用数据库
  runtimeOnly(libs.com.h2database.h2)
}
