package io.github.truenine.composeserver.testtoolkit

import io.github.truenine.composeserver.testtoolkit.utils.*
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
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.MountableFile

class TestcontainersVerificationTest {
  @Test
  fun `启动 Alpine 容器 输出日志并校验运行状态`() {
    log.info("开始测试 Testcontainers")

    GenericContainer("alpine:3.21")
      .apply {
        // 增加容器运行时间，确保有足够时间进行日志检查
        // 使用 flush 确保输出立即写入，避免缓冲延迟
        withCommand("sh", "-c", "echo 'Hello, Testcontainers!' && sync && sleep 10")
      }
      .use { container ->
        log.trace("正在启动测试容器...")
        container.startAndWaitForReady()

        log.info("容器状态: {}", container.isRunning)
        assertTrue("容器应该处于运行状态") { container.isRunning }

        // 使用重试机制等待日志内容出现，而不是立即检查
        // 这解决了日志缓冲和容器启动时序的竞态条件
        val expectedContent = "Hello, Testcontainers!"
        val logs =
          TestRetryUtils.waitUntilResult(
            timeout = Duration.ofSeconds(15),
            pollInterval = Duration.ofMillis(200),
            supplier = {
              val currentLogs = container.logs
              log.debug("当前日志内容: '{}'", currentLogs)
              currentLogs
            },
            condition = { logs ->
              val containsExpected = logs.contains(expectedContent)
              if (!containsExpected) {
                log.debug("日志尚未包含期望内容 '{}', 当前日志: '{}'", expectedContent, logs)
              }
              containsExpected
            },
          )

        log.info("容器日志: {}", logs)
        assertTrue("日志应该包含预期内容") { logs.contains(expectedContent) }
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

        // 充分验证文件内容
        assertEquals(testContent, actualContent, "文件内容应该匹配")
        assertTrue(actualContent.isNotEmpty(), "文件内容不应为空")
        assertTrue(actualContent.contains("测试文件"), "文件内容应包含预期的中文字符")
        assertEquals(testContent.length, actualContent.length, "文件长度应该匹配")

        // 验证文件确实存在
        assertTrue(container.fileExists(targetPath), "目标文件应该存在于容器中")

        log.info("文件复制和验证成功，内容: {} (长度: {})", actualContent, actualContent.length)
      }
  }

  @Test
  fun `启动 MySQL 容器 获取连接信息并校验运行状态`() {
    log.info("开始测试 MySQL 容器")

    MySQLContainer<Nothing>("mysql:8.0")
      .apply {
        withDatabaseName("testdb")
        withUsername("testuser")
        withPassword("testpass")
        withEnv("MYSQL_ROOT_PASSWORD", "rootpass")
        withExposedPorts(3306)
        withLogConsumer(Slf4jLogConsumer(log))
        withStartupTimeout(Duration.ofSeconds(120)) // 增加启动超时时间
      }
      .use { mysql ->
        log.info("正在启动 MySQL 容器...")
        mysql.start()

        // 使用重试机制等待容器完全就绪
        TestRetryUtils.waitUntil(timeout = Duration.ofSeconds(30), pollInterval = Duration.ofSeconds(1)) { mysql.isRunning && mysql.jdbcUrl.isNotEmpty() }

        assertTrue(mysql.isRunning, "MySQL 容器应该处于运行状态")
        log.info("MySQL 连接 URL: ${mysql.jdbcUrl}")
        log.info("MySQL 用户名: ${mysql.username}")
        log.info("MySQL 密码: ${mysql.password}")

        val expectedJdbcUrlPrefix = "jdbc:mysql://"
        assertTrue(mysql.jdbcUrl.startsWith(expectedJdbcUrlPrefix), "JDBC URL 应该以 $expectedJdbcUrlPrefix 开头")
      }
  }

  @Test
  fun `并发测试多个网址 任一完成即结束`() {
    assertTimeout(Duration.ofSeconds(20), "可能由于docker 网络原因导致测试失败，考虑检查 docker 网络配置") {
      runBlocking {
        log.info("开始并发测试多个网址连接")

        val urls = listOf("https://www.aliyun.com", "https://www.tencent.com", "https://www.baidu.com", "https://www.qq.com")

        GenericContainer("curlimages/curl:8.1.0")
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

                    // 验证命令执行结果 - 只有在有输出时才验证内容
                    if (statusCode.isNotEmpty()) {
                      assertTrue(result.stderr.isEmpty() || result.stderr.isBlank(), "命令错误输出应为空或仅包含空白字符")
                    }
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

            val testResultContent =
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

            resultFile.writeText(testResultContent)

            // 验证文件写入结果
            assertTrue(resultFile.exists(), "结果文件应该存在")
            assertTrue(resultFile.length() > 0, "结果文件大小应大于0")
            assertTrue(resultFile.canRead(), "结果文件应该可读")

            val writtenContent = resultFile.readText()
            assertEquals(testResultContent, writtenContent, "写入的内容应该与预期匹配")
            assertTrue(writtenContent.contains(firstResult.first), "文件内容应包含第一个完成的URL")
            assertTrue(writtenContent.contains("测试时间:"), "文件内容应包含时间戳信息")

            log.info("测试结果已写入文件: {} (大小: {} 字节)", resultFile.absolutePath, resultFile.length())
          }
      }
    }
  }
}
