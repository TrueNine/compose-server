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
