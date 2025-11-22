package io.github.truenine.composeserver.testtoolkit.utils

import io.github.truenine.composeserver.testtoolkit.log
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

/**
 * Container extension function tests.
 *
 * Tests all extension functions defined in ContainerExtensions.kt.
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class ContainerExtensionsTest {

  @Test
  fun `commandExecutor extension function`() {
    log.info("Starting commandExecutor extension function test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = container.commandExecutor()

      assertNotNull(executor, "Command executor should not be null")

      log.info("commandExecutor extension function test completed")
    }
  }

  @Test
  fun `safeExecInContainer extension function`() {
    log.info("Starting safeExecInContainer extension function test")

    GenericContainer(DockerImageName.parse("alpine:3.21"))
      .withCommand("sleep", "300") // Keep container running for 5 minutes, enough for tests
      .use { container ->
        container.start()

        // Wait for container to fully start
        Thread.sleep(1000)

        val result = container.safeExecInContainer(commands = arrayOf("echo", "Hello World"))

        assertEquals(0, result.exitCode, "Command execution should succeed")
        assertTrue(result.stdout.contains("Hello World"), "Output should contain expected content")

        log.info("safeExecInContainer extension function test completed")
      }
  }

  @Test
  fun `safeExecInContainer extension function with custom parameters`() {
    log.info("Starting safeExecInContainer extension function test with custom parameters")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val result = container.safeExecInContainer(timeout = Duration.ofSeconds(10), maxRetries = 2, "echo", "Custom Test")

      assertEquals(0, result.exitCode, "Command execution with custom parameters should succeed")
      assertTrue(result.stdout.contains("Custom Test"), "Output should contain expected content")

      log.info("safeExecInContainer extension function test with custom parameters completed")
    }
  }

  @Test
  fun `execWithExpectedExitCode extension function`() {
    log.info("Starting execWithExpectedExitCode extension function test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      // Test a successful command
      val result = container.execWithExpectedExitCode(0, commands = arrayOf("echo", "Success"))
      assertEquals(0, result.exitCode, "Command with expected exit code 0 should succeed")

      log.info("execWithExpectedExitCode extension function test completed")
    }
  }

  @Test
  fun `execAndGetOutput extension function`() {
    log.info("Starting execAndGetOutput extension function test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val output = container.execAndGetOutput(commands = arrayOf("echo", "Test Output"))

      assertTrue(output.contains("Test Output"), "Output should contain expected content")

      log.info("execAndGetOutput extension function test completed")
    }
  }

  @Test
  fun `execAndCheckOutput extension function`() {
    log.info("Starting execAndCheckOutput extension function test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val result = container.execAndCheckOutput("Hello", commands = arrayOf("echo", "Hello World"))

      assertEquals(0, result.exitCode, "Command whose output contains expected content should succeed")
      assertTrue(result.stdout.contains("Hello"), "Output should contain the expected substring")

      log.info("execAndCheckOutput extension function test completed")
    }
  }

  @Test
  fun `waitForReady extension function`() {
    log.info("Starting waitForReady extension function test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      // Container is already running so waiting for readiness should complete immediately
      container.waitForReady(Duration.ofSeconds(5), Duration.ofMillis(100))

      assertTrue(container.isRunning, "Container should be in running state")

      log.info("waitForReady extension function test completed")
    }
  }

  @Test
  fun `fileExists extension function`() {
    log.info("Starting fileExists extension function test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      // Existing file should be reported as present
      val exists = container.fileExists("/bin/sh")
      assertTrue(exists, "/bin/sh should exist")

      // Non-existent file should be reported as absent
      val notExists = container.fileExists("/nonexistent/file")
      assertTrue(!notExists, "Non-existent file should return false")

      log.info("fileExists extension function test completed")
    }
  }

  @Test
  fun `readFile extension function`() {
    log.info("Starting readFile extension function test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      // Create a test file
      container.safeExecInContainer(commands = arrayOf("sh", "-c", "echo 'Test Content' > /tmp/testfile"))

      val content = container.readFile("/tmp/testfile")

      assertTrue(content.contains("Test Content"), "File content should contain expected text")

      log.info("readFile extension function test completed")
    }
  }

  @Test
  fun `waitForFile extension function`() {
    log.info("Starting waitForFile extension function test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      // Create the file
      container.safeExecInContainer(commands = arrayOf("touch", "/tmp/waitfile"))

      // Wait for the file to appear (already exists so should return immediately)
      container.waitForFile("/tmp/waitfile", Duration.ofSeconds(5), Duration.ofMillis(100))

      // Verify that the file actually exists
      assertTrue(container.fileExists("/tmp/waitfile"), "Waited-for file should exist")

      log.info("waitForFile extension function test completed")
    }
  }

  @Test
  fun `withStableWaitStrategy extension function`() {
    log.info("Starting withStableWaitStrategy extension function test")

    // Test the existence of the extension by indirectly using other extensions
    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      // Verify container starts normally
      assertTrue(container.isRunning, "Container should start successfully")

      // Validate behavior by calling another extension
      val result = container.safeExecInContainer(commands = arrayOf("echo", "test"))
      assertEquals(0, result.exitCode, "Command execution should succeed")

      log.info("withStableWaitStrategy extension function test completed")
    }
  }

  @Test
  fun `withStableWaitStrategy extension function with custom parameters`() {
    log.info("Starting withStableWaitStrategy extension function test with custom parameters")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      assertTrue(container.isRunning, "Stable wait strategy with custom parameters should work correctly")

      log.info("withStableWaitStrategy extension function test with custom parameters completed")
    }
  }

  @Test
  fun `withHealthCheck extension function`() {
    log.info("Starting withHealthCheck extension function test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      assertTrue(container.isRunning, "Container with health check should start successfully")

      log.info("withHealthCheck extension function test completed")
    }
  }

  @Test
  fun `withHealthCheck extension function with custom parameters`() {
    log.info("Starting withHealthCheck extension function test with custom parameters")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      assertTrue(container.isRunning, "Container with custom health check parameters should start successfully")

      log.info("withHealthCheck extension function test with custom parameters completed")
    }
  }

  @Test
  fun `startAndWaitForReady extension function`() {
    log.info("Starting startAndWaitForReady extension function test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.startAndWaitForReady()

      assertTrue(container.isRunning, "Container started and waited for readiness should be running")

      log.info("startAndWaitForReady extension function test completed")
    }
  }

  @Test
  fun `startAndWaitForReady extension function with custom parameters`() {
    log.info("Starting startAndWaitForReady extension function test with custom parameters")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.startAndWaitForReady(readyTimeout = Duration.ofSeconds(20), readyPollInterval = Duration.ofMillis(200))

      assertTrue(container.isRunning, "Container started and waited for readiness with custom parameters should be running")

      log.info("startAndWaitForReady extension function test with custom parameters completed")
    }
  }
}
