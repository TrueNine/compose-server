package io.github.truenine.composeserver.testtoolkit.utils

import io.github.truenine.composeserver.testtoolkit.log
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

/**
 * Container command executor tests.
 *
 * Tests all features of ContainerCommandExecutor to ensure full coverage.
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class ContainerCommandExecutorTest {

  @Test
  fun `ContainerCommandExecutor constructor and constants`() {
    log.info("Starting ContainerCommandExecutor constructor and constants test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)
      assertNotNull(executor, "Command executor should not be null")

      // Test constant values
      assertEquals(Duration.ofSeconds(30), ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT, "Default timeout should be 30 seconds")
      assertEquals(3, ContainerCommandExecutor.DEFAULT_MAX_RETRIES, "Default max retries should be 3")

      log.info("ContainerCommandExecutor constructor and constants test completed")
    }
  }

  @Test
  fun `executeCommand executes successfully`() {
    log.info("Starting executeCommand success test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)
      val result = executor.executeCommand(commands = arrayOf("echo", "Hello World"))

      assertEquals(0, result.exitCode, "Command execution should succeed")
      assertTrue(result.stdout.contains("Hello World"), "Output should contain expected content")

      log.info("executeCommand success test completed")
    }
  }

  @Test
  fun `executeCommand validates empty commands`() {
    log.info("Starting executeCommand empty commands validation test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      assertFailsWith<IllegalArgumentException> { executor.executeCommand() }

      log.info("executeCommand empty commands validation test completed")
    }
  }

  @Test
  fun `executeCommand with custom parameters`() {
    log.info("Starting executeCommand custom parameters test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)
      val result = executor.executeCommand(timeout = Duration.ofSeconds(10), maxRetries = 2, "echo", "Custom Test")

      assertEquals(0, result.exitCode, "Command execution with custom parameters should succeed")
      assertTrue(result.stdout.contains("Custom Test"), "Output should contain expected content")

      log.info("executeCommand custom parameters test completed")
    }
  }

  @Test
  fun `executeCommandWithExpectedExitCode success case`() {
    log.info("Starting executeCommandWithExpectedExitCode success test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)
      val result = executor.executeCommandWithExpectedExitCode(0, commands = arrayOf("echo", "Success"))

      assertEquals(0, result.exitCode, "Command with expected exit code 0 should succeed")
      assertTrue(result.stdout.contains("Success"), "Output should contain expected content")

      log.info("executeCommandWithExpectedExitCode success test completed")
    }
  }

  @Test
  fun `executeCommandWithExpectedExitCode failure case`() {
    log.info("Starting executeCommandWithExpectedExitCode failure test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      assertFailsWith<AssertionError> { executor.executeCommandWithExpectedExitCode(0, commands = arrayOf("sh", "-c", "exit 1")) }

      log.info("executeCommandWithExpectedExitCode failure test completed")
    }
  }

  @Test
  fun `executeCommandAndGetOutput`() {
    log.info("Starting executeCommandAndGetOutput test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)
      val output = executor.executeCommandAndGetOutput(commands = arrayOf("echo", "Test Output"))

      assertEquals("Test Output", output, "Output should exactly match expected content (trimmed)")

      log.info("executeCommandAndGetOutput test completed")
    }
  }

  @Test
  fun `executeCommandAndCheckOutput success case`() {
    log.info("Starting executeCommandAndCheckOutput success test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)
      val result = executor.executeCommandAndCheckOutput("Hello", commands = arrayOf("echo", "Hello World"))

      assertEquals(0, result.exitCode, "Command whose output contains expected content should succeed")
      assertTrue(result.stdout.contains("Hello"), "Output should contain the expected substring")

      log.info("executeCommandAndCheckOutput success test completed")
    }
  }

  @Test
  fun `executeCommandAndCheckOutput failure case`() {
    log.info("Starting executeCommandAndCheckOutput failure test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      assertFailsWith<AssertionError> { executor.executeCommandAndCheckOutput("NotFound", commands = arrayOf("echo", "Hello World")) }

      log.info("executeCommandAndCheckOutput failure test completed")
    }
  }

  @Test
  fun `waitForContainerReady`() {
    log.info("Starting waitForContainerReady test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      // Container is already running so waiting for readiness should complete immediately
      executor.waitForContainerReady(Duration.ofSeconds(5), Duration.ofMillis(100))

      assertTrue(container.isRunning, "Container should be in running state")

      log.info("waitForContainerReady test completed")
    }
  }

  @Test
  fun `fileExists for existing files`() {
    log.info("Starting fileExists existing files test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      assertTrue(executor.fileExists("/bin/sh"), "/bin/sh should exist")
      assertTrue(executor.fileExists("/etc/passwd"), "/etc/passwd should exist")

      log.info("fileExists existing files test completed")
    }
  }

  @Test
  fun `fileExists for non-existent files`() {
    log.info("Starting fileExists non-existent files test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      assertFalse(executor.fileExists("/nonexistent/file"), "Non-existent file should return false")
      assertFalse(executor.fileExists("/tmp/does-not-exist"), "Non-existent file should return false")

      log.info("fileExists non-existent files test completed")
    }
  }

  @Test
  fun `waitForFile`() {
    log.info("Starting waitForFile test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      // Create file
      executor.executeCommand(commands = arrayOf("touch", "/tmp/waitfile"))

      // Wait for the file to appear (already exists so should return immediately)
      executor.waitForFile("/tmp/waitfile", Duration.ofSeconds(5), Duration.ofMillis(100))

      assertTrue(executor.fileExists("/tmp/waitfile"), "Waited-for file should exist")

      log.info("waitForFile test completed")
    }
  }

  @Test
  fun `readFileContent`() {
    log.info("Starting readFileContent test")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      // Create a test file
      executor.executeCommand(commands = arrayOf("sh", "-c", "echo 'Test File Content' > /tmp/testfile"))

      val content = executor.readFileContent("/tmp/testfile")

      assertEquals("Test File Content", content, "File content should match expected text")

      log.info("readFileContent test completed")
    }
  }
}
