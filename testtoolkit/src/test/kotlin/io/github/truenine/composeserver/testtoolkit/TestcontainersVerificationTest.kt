package io.github.truenine.composeserver.testtoolkit

import io.github.truenine.composeserver.testtoolkit.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeout
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.MountableFile
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.createTempFile
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestcontainersVerificationTest {
  @Test
  fun `启动 Alpine 容器 输出日志并校验运行状态`() {
    log.info("开始测试 Testcontainers")

    GenericContainer("alpine:latest")
      .apply { withCommand("sh", "-c", "echo 'Hello, Testcontainers!' && sleep 1") }
      .use { container ->
        log.trace("正在启动测试容器...")
        container.startAndWaitForReady()

        log.info("容器状态: {}", container.isRunning)
        assertTrue("容器应该处于运行状态") { container.isRunning }

        val logs = container.logs
        log.info("容器日志: {}", logs)
        assertTrue("日志应该包含预期内容") { logs.contains("Hello, Testcontainers!") }
      }
  }

  @Test
  fun `启动 PostgreSQL 容器 获取连接信息并校验运行状态`() {
    log.info("开始测试 PostgreSQL 容器")

    PostgreSQLContainer<Nothing>("postgres:16-alpine")
      .apply {
        withDatabaseName("testdb")
        withUsername("testuser")
        withPassword("testpass")
        withExposedPorts(5432)
        withLogConsumer(Slf4jLogConsumer(log))
        withStartupTimeout(Duration.ofSeconds(120)) // 增加启动超时时间
      }
      .use { postgres ->
        log.info("正在启动 PostgreSQL 容器...")
        postgres.start()

        // 使用重试机制等待容器完全就绪
        TestRetryUtils.waitUntil(timeout = Duration.ofSeconds(30), pollInterval = Duration.ofSeconds(1)) { postgres.isRunning && postgres.jdbcUrl.isNotEmpty() }

        assertTrue(postgres.isRunning, "PostgreSQL 容器应该处于运行状态")
        log.info("PostgreSQL 连接 URL: ${postgres.jdbcUrl}")
        log.info("PostgreSQL 用户名: ${postgres.username}")
        log.info("PostgreSQL 密码: ${postgres.password}")

        val expectedJdbcUrlPrefix = "jdbc:postgresql://"
        assertTrue(postgres.jdbcUrl.startsWith(expectedJdbcUrlPrefix), "JDBC URL 应该以 $expectedJdbcUrlPrefix 开头")
      }
  }

  @Test
  fun `在 Alpine 容器内执行命令 返回预期输出`() {
    log.info("开始测试容器命令执行")

    GenericContainer("alpine:3.19.1")
      .apply { withCommand("tail", "-f", "/dev/null") }
      .use { container ->
        container.startAndWaitForReady()

        // 使用新的扩展方法执行命令，自动处理重试和 null exitCode 问题
        val result =
          container.execAndCheckOutput(
            expectedContent = "Hello from Alpine",
            timeout = Duration.ofSeconds(30),
            maxRetries = 5,
            "sh",
            "-c",
            "echo 'Hello from Alpine' && uname -a",
          )

        log.info("命令执行结果: ${result.stdout}")
        assertEquals(0, result.exitCode, "命令应该成功执行")
      }
  }

  @Test
  fun `向 Alpine 容器复制文件 校验内容一致`() {
    log.info("开始测试容器文件复制")
    GenericContainer("alpine:3.19.1")
      .apply { withCommand("tail", "-f", "/dev/null") }
      .use { container ->
        container.startAndWaitForReady()

        // 创建测试文件内容
        val testContent = "这是一个测试文件内容"
        val targetPath = "/tmp/test-file.txt"

        // 使用 MountableFile 复制内容到容器内的文件
        val tempFile = createTempFile().toFile()
        tempFile.writeText(testContent)
        val mountFile = MountableFile.forHostPath(tempFile.absolutePath)
        container.copyFileToContainer(mountFile, targetPath)

        // 等待文件出现并验证内容
        container.waitForFile(targetPath, Duration.ofSeconds(10))

        val actualContent = container.readFile(filePath = targetPath, timeout = Duration.ofSeconds(30), maxRetries = 5)

        assertEquals(testContent, actualContent, "文件内容应该匹配")
        log.info("文件复制和验证成功，内容: {}", actualContent)
      }
  }

  @Test
  fun `并发测试多个网址 任一完成即结束`() {
    assertTimeout(Duration.ofSeconds(20), "可能由于docker 网络原因导致测试失败，考虑检查 docker 网络配置") {
      runBlocking {
        log.info("开始并发测试多个网址连接")

        val urls = listOf("https://www.aliyun.com", "https://www.tencent.com", "https://www.baidu.com", "https://www.qq.com")

        GenericContainer("alpine/curl:latest")
          .apply {
            withCommand("sleep", "30") // 增加容器运行时间
          }
          .use { container ->
            container.startAndWaitForReady(Duration.ofSeconds(30))

            // 为每个URL创建一个协程，使用改进的命令执行方法
            val results =
              urls.map { url ->
                async(Dispatchers.IO) {
                  try {
                    // 使用更宽松的命令执行，不强制要求退出码为0
                    val result =
                      TestRetryUtils.retryUntilSuccess(timeout = Duration.ofSeconds(10), pollInterval = Duration.ofMillis(500)) {
                        container.execInContainer("curl", "-s", "-o", "/dev/null", "-w", "%{http_code}", "-m", "8", url)
                      }

                    // 检查是否成功获取到HTTP状态码
                    val statusCode = result.stdout.trim()
                    if (statusCode.matches(Regex("\\d{3}"))) {
                      log.info("URL: $url, 状态码: $statusCode")
                      url to statusCode
                    } else {
                      log.warn("URL: $url 未获取到有效状态码，退出码: ${result.exitCode}, 输出: ${result.stdout}")
                      url to "timeout"
                    }
                  } catch (e: Exception) {
                    log.warn("URL: $url 测试失败: ${e.message}")
                    url to "error"
                  }
                }
              }

            // 使用 select 等待第一个完成的响应（无论成功失败）
            val firstResult = select { results.forEach { deferred -> deferred.onAwait { result -> result } } }

            log.info("第一个完成的URL: {} 状态: {}", firstResult.first, firstResult.second)

            // 写入测试结果到文件
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val resultFile = createTempFile("test_result_${timestamp}_", ".txt").toFile()

            resultFile.writeText(
              """
              |测试时间: ${LocalDateTime.now()}
              |测试URL列表:
              |${urls.joinToString("\n") { "- $it" }}
              |
              |测试结果:
              |第一个完成URL: ${firstResult.first}
              |状态码: ${firstResult.second}
            """
                .trimMargin()
            )
            log.info("测试结果已写入文件: {}", resultFile.absolutePath)
          }
      }
    }
  }
}
