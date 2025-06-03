package net.yan100.compose.testtoolkit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeout
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.MountableFile
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.createTempFile
import kotlin.test.assertEquals

class TestcontainersVerificationTest {
  private val logger = LoggerFactory.getLogger(TestcontainersVerificationTest::class.java)

  @Test
  fun `启动 Alpine 容器 输出日志并校验运行状态`() {
    logger.info("开始测试 Testcontainers")

    GenericContainer("alpine:3.19.1").apply {
      withCommand("sh", "-c", "echo 'Hello, Testcontainers!' && sleep 1")
    }.use { container ->
      logger.info("正在启动测试容器...")
      container.start()

      logger.info("容器状态: ${container.isRunning}")
      Assertions.assertTrue(container.isRunning, "容器应该处于运行状态")

      val logs = container.logs
      logger.info("容器日志: $logs")

      logger.info("测试完成")
    }
  }

  @Test
  fun `启动 PostgreSQL 容器 获取连接信息并校验运行状态`() {
    logger.info("开始测试 PostgreSQL 容器")

    PostgreSQLContainer<Nothing>("postgres:16-alpine").apply {
      withDatabaseName("testdb")
      withUsername("testuser")
      withPassword("testpass")
      withExposedPorts(5432)
      withLogConsumer(Slf4jLogConsumer(logger))
    }.use { postgres ->
      logger.info("正在启动 PostgreSQL 容器...")
      postgres.start()

      Assertions.assertTrue(postgres.isRunning, "PostgreSQL 容器应该处于运行状态")
      logger.info("PostgreSQL 连接 URL: ${postgres.jdbcUrl}")
      logger.info("PostgreSQL 用户名: ${postgres.username}")
      logger.info("PostgreSQL 密码: ${postgres.password}")

      val expectedJdbcUrlPrefix = "jdbc:postgresql://"
      Assertions.assertTrue(
        postgres.jdbcUrl.startsWith(expectedJdbcUrlPrefix), "JDBC URL 应该以 $expectedJdbcUrlPrefix 开头"
      )

      logger.info("PostgreSQL 测试完成")
    }
  }

  @Test
  fun `在 Alpine 容器内执行命令 返回预期输出`() {
    logger.info("开始测试容器命令执行")

    GenericContainer("alpine:3.19.1").apply {
      withCommand("tail", "-f", "/dev/null")
    }.use { container ->
      container.start()

      // 执行命令并获取结果
      val result = container.execInContainer("sh", "-c", "echo 'Hello from Alpine' && uname -a")

      Assertions.assertEquals(0, result.exitCode, "命令应该成功执行")
      Assertions.assertTrue(result.stdout.contains("Hello from Alpine"), "输出应该包含预期内容")
      logger.info("命令执行结果: ${result.stdout}")

      logger.info("命令执行测试完成")
    }
  }

  @Test
  fun `向 Alpine 容器复制文件 校验内容一致`() {
    logger.info("开始测试容器文件复制")

    GenericContainer("alpine:3.19.1").apply {
      withCommand("tail", "-f", "/dev/null")
    }.use { container ->
      container.start()

      // 创建测试文件内容
      val testContent = "这是一个测试文件内容"

      // 使用 MountableFile 复制内容到容器内的文件
      val tempFile = createTempFile().toFile()
      tempFile.writeText(testContent)
      val mountFile = MountableFile.forHostPath(tempFile.absolutePath)
      container.copyFileToContainer(mountFile, "/tmp/test-file.txt")

      // 验证文件内容
      val result = container.execInContainer("cat", "/tmp/test-file.txt")
      Assertions.assertEquals(0, result.exitCode, "文件应该存在并可被读取")
      Assertions.assertEquals(testContent, result.stdout.trim(), "文件内容应该匹配")

      logger.info("文件复制测试完成")
    }
  }

  @Test
  fun `并发测试多个网址 任一成功即结束`() = runBlocking {
    assertTimeout(
      Duration.ofSeconds(10), "可能由于docker 网络原因导致测试失败，考虑检查 docker 网络配置"
    ) {
      logger.info("开始并发测试多个网址连接")

      val urls = listOf(
        "https://www.aliyun.com",
        "https://www.tencent.com"
      )

      GenericContainer("alpine/curl:latest").apply {
        withCommand("sleep", "60") // 保证容器一直运行
      }.use { container ->
        container.start()

        // 为每个URL创建一个协程，使用 withContext(Dispatchers.IO) 确保真正的并行执行
        val results = urls.map { url ->
          async(Dispatchers.IO) {
            try {
              val result = container.execInContainer(
                "curl", "-s", "-o", "/dev/null", "-w", "%{http_code}", "-m", "5", url
              )
              logger.info("URL: $url, exitCode: ${result.exitCode}, stdout: ${result.stdout}, stderr: ${result.stderr}")
              url to result.stdout.trim()
            } catch (e: Exception) {
              logger.error("URL: $url 测试失败: ${e.message}", e)
              url to "error"
            }
          }
        }

        // 使用 select 等待第一个成功的响应
        val successfulResult = runBlocking {
          select {
            results.forEach { deferred ->
              deferred.onAwait { result ->
                if (result.second == "200") {
                  result
                } else {
                  logger.warn("URL: ${result.first} 返回非200状态码: ${result.second}")
                  null
                }
              }
            }
          }
        }

        requireNotNull(successfulResult) { 
          val failedResults = results.map { it.getCompleted() }
          "没有找到成功的响应。所有URL测试结果：\n" + 
          failedResults.joinToString("\n") { (url, status) -> 
            "- $url: $status" 
          }
        }
        
        logger.info("成功响应URL: ${successfulResult.first}")
        assertEquals("200", successfulResult.second, "应该返回 HTTP 200 状态码")

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
            |成功URL: ${successfulResult.first}
            |状态码: ${successfulResult.second}
          """.trimMargin()
        )

        logger.info("测试结果已写入文件: ${resultFile.absolutePath}")
        logger.info("并发网络连接测试完成")
      }
    }
  }
}
