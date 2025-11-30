package io.github.truenine.composeserver.testtoolkit

import io.github.truenine.composeserver.testtoolkit.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeout
import org.testcontainers.containers.*
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
  fun `start Alpine container and verify logs and running state`() {
    log.info("Starting Testcontainers verification test")

    GenericContainer("alpine:3.21")
      .apply {
        // Increase container runtime to allow enough time to inspect logs
        // Use sync to flush output immediately and avoid buffering delays
        withCommand("sh", "-c", "echo 'Hello, Testcontainers!' && sync && sleep 10")
      }
      .use { container ->
        log.trace("Starting test container...")
        container.startAndWaitForReady()

        log.info("Container running state: {}", container.isRunning)
        assertTrue("Container should be in running state") { container.isRunning }

        // Use retry mechanism to wait for log content instead of checking immediately
        // This avoids race conditions from log buffering and container startup timing
        val expectedContent = "Hello, Testcontainers!"
        val logs =
          TestRetryUtils.waitUntilResult(
            timeout = Duration.ofSeconds(15),
            pollInterval = Duration.ofMillis(200),
            supplier = {
              val currentLogs = container.logs
              log.debug("Current log content: '{}'", currentLogs)
              currentLogs
            },
            condition = { logs ->
              val containsExpected = logs.contains(expectedContent)
              if (!containsExpected) {
                log.debug("Logs do not yet contain expected content '{}', current logs: '{}'", expectedContent, logs)
              }
              containsExpected
            },
          )

        log.info("Container logs: {}", logs)
        assertTrue("Logs should contain expected content") { logs.contains(expectedContent) }
      }
  }

  @Test
  fun `start PostgreSQL container and verify running state`() {
    log.info("Starting PostgreSQL container test")

    PostgreSQLContainer<Nothing>("postgres:16-alpine")
      .apply {
        withDatabaseName("testdb")
        withUsername("testuser")
        withPassword("testpass")
        withExposedPorts(5432)
        withLogConsumer(Slf4jLogConsumer(log))
        withStartupTimeout(Duration.ofSeconds(120)) // Increase startup timeout
      }
      .use { postgres ->
        log.info("Starting PostgreSQL container...")
        postgres.startAndWaitForReady(Duration.ofSeconds(90))

        // Use retry utility to wait until the container is fully ready
        TestRetryUtils.waitUntil(timeout = Duration.ofSeconds(30), pollInterval = Duration.ofSeconds(1)) { postgres.isRunning && postgres.jdbcUrl.isNotEmpty() }

        assertTrue(postgres.isRunning, "PostgreSQL container should be in running state")
        log.info("PostgreSQL JDBC URL: ${postgres.jdbcUrl}")
        log.info("PostgreSQL username: ${postgres.username}")
        log.info("PostgreSQL password: ${postgres.password}")

        val expectedJdbcUrlPrefix = "jdbc:postgresql://"
        assertTrue(postgres.jdbcUrl.startsWith(expectedJdbcUrlPrefix), "JDBC URL should start with $expectedJdbcUrlPrefix")
      }
  }

  @Test
  fun `execute command in Alpine container and validate output`() {
    log.info("Starting container command execution test")

    GenericContainer("alpine:3.21")
      .apply { withCommand("tail", "-f", "/dev/null") }
      .use { container ->
        container.startAndWaitForReady()

        // Use the new extension method to execute the command, automatically handling retries and null exitCode issues
        val result =
          container.execAndCheckOutput(
            expectedContent = "Hello from Alpine",
            timeout = Duration.ofSeconds(30),
            maxRetries = 5,
            "sh",
            "-c",
            "echo 'Hello from Alpine' && uname -a",
          )

        log.info("Command execution result: ${result.stdout}")
        assertEquals(0, result.exitCode, "Command should execute successfully")
      }
  }

  @Test
  fun `copy file into Alpine container and verify content`() {
    log.info("Starting container file copy test")
    GenericContainer("alpine:3.19.1")
      .apply { withCommand("tail", "-f", "/dev/null") }
      .use { container ->
        container.startAndWaitForReady()

        // Create test file content
        val testContent = "This is a test file content"
        val targetPath = "/tmp/test-file.txt"

        // Use MountableFile to copy content into a file inside the container
        val tempFile = createTempFile().toFile()
        tempFile.writeText(testContent)
        val mountFile = MountableFile.forHostPath(tempFile.absolutePath)
        container.copyFileToContainer(mountFile, targetPath)

        // Wait for the file to appear and verify its content
        container.waitForFile(targetPath, Duration.ofSeconds(10))

        val actualContent = container.readFile(filePath = targetPath, timeout = Duration.ofSeconds(30), maxRetries = 5)

        // Thoroughly verify file content
        assertEquals(testContent, actualContent, "File content should match")
        assertTrue(actualContent.isNotEmpty(), "File content should not be empty")
        assertTrue(actualContent.contains("test file"), "File content should contain the expected substring")
        assertEquals(testContent.length, actualContent.length, "File length should match")

        // Verify the file actually exists
        assertTrue(container.fileExists(targetPath), "Target file should exist in the container")

        log.info("File copy and verification successful, content: {} (length: {})", actualContent, actualContent.length)
      }
  }

  @Test
  fun `start MySQL container and verify running state`() {
    log.info("Starting MySQL container test")

    MySQLContainer<Nothing>("mysql:8.0")
      .apply {
        withDatabaseName("testdb")
        withUsername("testuser")
        withPassword("testpass")
        withEnv("MYSQL_ROOT_PASSWORD", "rootpass")
        withExposedPorts(3306)
        withLogConsumer(Slf4jLogConsumer(log))
        // Increase startup timeout
        withStartupTimeout(Duration.ofSeconds(120))
      }
      .use { mysql ->
        log.info("Starting MySQL container...")
        mysql.start()

        // Use retry utility to wait until the container is fully ready
        TestRetryUtils.waitUntil(timeout = Duration.ofSeconds(30), pollInterval = Duration.ofSeconds(1)) { mysql.isRunning && mysql.jdbcUrl.isNotEmpty() }

        assertTrue(mysql.isRunning, "MySQL container should be in running state")
        log.info("MySQL JDBC URL: ${mysql.jdbcUrl}")
        log.info("MySQL username: ${mysql.username}")
        log.info("MySQL password: ${mysql.password}")

        val expectedJdbcUrlPrefix = "jdbc:mysql://"
        assertTrue(mysql.jdbcUrl.startsWith(expectedJdbcUrlPrefix), "JDBC URL should start with $expectedJdbcUrlPrefix")
      }
  }

  @Test
  fun `concurrent test for multiple URLs finishes when any completes`() {
    assertTimeout(Duration.ofSeconds(20), "Test may fail due to Docker network issues; consider checking Docker network configuration") {
      runBlocking {
        log.info("Starting concurrent test for multiple URL connections")

        val urls = listOf("https://www.aliyun.com", "https://www.tencent.com", "https://www.baidu.com", "https://www.qq.com")

        GenericContainer("curlimages/curl:8.1.0")
          .apply {
            // Increase container runtime
            withCommand("sleep", "30")
          }
          .use { container ->
            container.startAndWaitForReady(Duration.ofSeconds(30))

            // Create a coroutine for each URL using the improved command execution
            val results =
              urls.map { url ->
                async(Dispatchers.IO) {
                  try {
                    // Use more tolerant command execution without requiring exit code 0
                    val result =
                      TestRetryUtils.retryUntilSuccess(timeout = Duration.ofSeconds(10), pollInterval = Duration.ofMillis(500)) {
                        container.execInContainer("curl", "-s", "-o", "/dev/null", "-w", "%{http_code}", "-m", "8", url)
                      }

                    // Check whether a HTTP status code was retrieved successfully
                    val statusCode = result.stdout.trim()

                    // Validate command result only when there is output
                    if (statusCode.isNotEmpty()) {
                      assertTrue(result.stderr.isEmpty() || result.stderr.isBlank(), "Command stderr should be empty or contain only whitespace")
                    }
                    if (statusCode.matches(Regex("\\d{3}"))) {
                      log.info("URL: $url, status code: $statusCode")
                      url to statusCode
                    } else {
                      log.warn("URL: $url did not return a valid status code, exit code: ${result.exitCode}, output: ${result.stdout}")
                      url to "timeout"
                    }
                  } catch (e: Exception) {
                    log.warn("URL: $url test failed: ${e.message}")
                    url to "error"
                  }
                }
              }

            // Use select to wait for the first completed response (regardless of success or failure)
            val firstResult = select { results.forEach { deferred -> deferred.onAwait { result -> result } } }

            log.info("First completed URL: {} status: {}", firstResult.first, firstResult.second)

            // Write test result to file
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val resultFile = createTempFile("test_result_${timestamp}_", ".txt").toFile()

            val testResultContent =
              """
              |Test time: ${LocalDateTime.now()}
              |Test URL list:
              |${urls.joinToString("\n") { "- $it" }}
              |
              |Test result:
              |First completed URL: ${firstResult.first}
              |Status code: ${firstResult.second}
            """
                .trimMargin()

            resultFile.writeText(testResultContent)

            // Verify file write result
            assertTrue(resultFile.exists(), "Result file should exist")
            assertTrue(resultFile.length() > 0, "Result file size should be greater than 0")
            assertTrue(resultFile.canRead(), "Result file should be readable")

            val writtenContent = resultFile.readText()
            assertEquals(testResultContent, writtenContent, "Written content should match expected text")
            assertTrue(writtenContent.contains(firstResult.first), "File content should contain the first completed URL")
            assertTrue(writtenContent.contains("Test time:"), "File content should contain timestamp information")

            log.info("Test result written to file: {} (size: {} bytes)", resultFile.absolutePath, resultFile.length())
          }
      }
    }
  }
}
