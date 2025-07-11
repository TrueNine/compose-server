package io.github.truenine.composeserver.testtoolkit

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.createTempFile
import kotlin.test.assertEquals
import kotlin.test.assertTrue
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

class TestcontainersVerificationTest {
  @Test
  fun `启动 Alpine 容器 输出日志并校验运行状态`() {
    log.info("开始测试 Testcontainers")

    GenericContainer("alpine:latest")
      .apply { withCommand("sh", "-c", "echo 'Hello, Testcontainers!' && sleep 1") }
      .use { container ->
        log.trace("正在启动测试容器...")
        container.start()

        log.info("容器状态: {}", container.isRunning)
        assertTrue("容器应该处于运行状态") { container.isRunning }

        val logs = container.logs
        log.info("容器日志: {}", logs)
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
      }
      .use { postgres ->
        log.info("正在启动 PostgreSQL 容器...")
        postgres.start()

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
        container.start()

        // 执行命令并获取结果 - 处理 exitCode 为 null 的问题
        try {
          val result = container.execInContainer("sh", "-c", "echo 'Hello from Alpine' && uname -a")
          assertEquals(0, result.exitCode, "命令应该成功执行")
          assertTrue(result.stdout.contains("Hello from Alpine"), "输出应该包含预期内容")
          log.info("命令执行结果: ${result.stdout}")
        } catch (e: NullPointerException) {
          log.warn("执行命令时遇到退出码为 null 的问题，尝试重新执行: ${e.message}")
          Thread.sleep(100) // 短暂等待
          val retryResult = container.execInContainer("sh", "-c", "echo 'Hello from Alpine' && uname -a")
          assertTrue(retryResult.stdout.contains("Hello from Alpine"), "输出应该包含预期内容")
          log.info("重试命令执行结果: ${retryResult.stdout}")
        }
      }
  }

  @Test
  fun `向 Alpine 容器复制文件 校验内容一致`() {
    log.info("开始测试容器文件复制")
    GenericContainer("alpine:3.19.1")
      .apply { withCommand("tail", "-f", "/dev/null") }
      .use { container ->
        container.start()

        // 创建测试文件内容
        val testContent = "这是一个测试文件内容"

        // 使用 MountableFile 复制内容到容器内的文件
        val tempFile = createTempFile().toFile()
        tempFile.writeText(testContent)
        val mountFile = MountableFile.forHostPath(tempFile.absolutePath)
        container.copyFileToContainer(mountFile, "/tmp/test-file.txt")

        // 验证文件内容 - 使用重试机制处理可能的 null 退出码问题
        var retryCount = 0
        val maxRetries = 3
        var lastException: Exception? = null

        while (retryCount < maxRetries) {
          try {
            val result = container.execInContainer("cat", "/tmp/test-file.txt")
            assertEquals(testContent, result.stdout.trim(), "文件内容应该匹配")
            return // 成功执行，退出测试
          } catch (e: NullPointerException) {
            lastException = e
            retryCount++
            log.warn("执行命令时遇到退出码为 null 的问题，第 $retryCount 次重试: ${e.message}")

            if (retryCount < maxRetries) {
              Thread.sleep((200 * retryCount).toLong()) // 递增等待时间
            }
          } catch (e: Exception) {
            // 其他异常直接抛出
            throw e
          }
        }

        // 如果重试都失败了，抛出最后一个异常
        throw AssertionError("执行命令失败，已重试 $maxRetries 次", lastException)
      }
  }

  @Test
  fun `并发测试多个网址 任一完成即结束`() {
    assertTimeout(Duration.ofSeconds(10), "可能由于docker 网络原因导致测试失败，考虑检查 docker 网络配置") {
      runBlocking {
        log.info("开始并发测试多个网址连接")

        val urls = listOf("https://www.aliyun.com", "https://www.tencent.com", "https://www.baidu.com", "https://www.qq.com")

        GenericContainer("alpine/curl:latest")
          .apply {
            withCommand("sleep", "10") // 保证容器一直运行
          }
          .use { container ->
            container.start()

            // 为每个URL创建一个协程，使用 withContext(Dispatchers.IO) 确保真正的并行执行
            val results =
              urls.map { url ->
                async(Dispatchers.IO) {
                  try {
                    val result = container.execInContainer("curl", "-s", "-o", "/dev/null", "-w", "%{http_code}", "-m", "5", url)
                    log.info("URL: $url, exitCode: ${result.exitCode}, stdout: ${result.stdout}, stderr: ${result.stderr}")
                    url to result.stdout.trim()
                  } catch (e: NullPointerException) {
                    log.warn("URL: $url 执行命令时遇到退出码为 null 的问题，尝试重新执行: ${e.message}")
                    try {
                      Thread.sleep(100) // 短暂等待
                      val retryResult = container.execInContainer("curl", "-s", "-o", "/dev/null", "-w", "%{http_code}", "-m", "5", url)
                      log.info("URL: $url 重试结果, stdout: ${retryResult.stdout}, stderr: ${retryResult.stderr}")
                      url to retryResult.stdout.trim()
                    } catch (retryE: Exception) {
                      log.error("URL: $url 重试失败: ${retryE.message}", retryE)
                      url to "error"
                    }
                  } catch (e: Exception) {
                    log.error("URL: $url 测试失败: ${e.message}", e)
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
