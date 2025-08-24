plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
WeChat Official Account Platform SDK implementation providing comprehensive APIs for WeChat public platform integration.
Includes authentication, user information retrieval, access token management, and JSAPI signature generation capabilities.
"""
    .trimIndent()

dependencies {
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)

  api(projects.shared)
  implementation(projects.depend.dependHttpExchange)
  implementation(projects.depend.dependJackson)
  implementation(libs.org.springframework.boot.spring.boot.starter.webflux)

  implementation(projects.security.securityCrypto)
  implementation(libs.org.springframework.security.spring.security.core)

  testImplementation(libs.org.springframework.boot.spring.boot.starter.webflux)
  testImplementation(projects.testtoolkit.testtoolkitShared)
}

// 直接实现 dotenv 功能，避免插件依赖
fun loadDotenv() {
  val envFile = rootProject.file(".env")
  if (envFile.exists()) {
    envFile.readLines().forEach { line ->
      val trimmedLine = line.trim()
      if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#")) {
        val parts = trimmedLine.split("=", limit = 2)
        if (parts.size == 2) {
          val key = parts[0].trim()
          val value = parts[1].trim().removeSurrounding("\"").removeSurrounding("'")
          if (key.isNotEmpty() && value.isNotEmpty()) {
            // 设置到所有任务的环境变量中
            tasks.withType<Test> { environment(key, value) }
            tasks.withType<JavaExec> { environment(key, value) }
            logger.debug("Loaded environment variable: $key")
          }
        }
      }
    }
    logger.info("Loaded .env file from: ${envFile.absolutePath}")
  } else {
    logger.warn(".env file not found at: ${envFile.absolutePath}")
  }
}

// 加载 dotenv 配置
loadDotenv()
