package io.github.truenine.composeserver.testtoolkit.utils

import java.time.Duration
import org.slf4j.LoggerFactory
import org.testcontainers.containers.Container
import org.testcontainers.containers.GenericContainer

/**
 * Container command execution utility.
 *
 * Provides stable command execution for containers, integrating retry mechanisms and error handling.
 * Resolves the issue where Testcontainers execInContainer may return a null exit code.
 *
 * Features:
 * - Automatic retry mechanism to handle null exit code issues
 * - Unified error handling and logging
 * - Supports custom timeout and retry configuration
 * - Provides convenient command execution methods
 *
 * Usage example:
 *
 * ```kotlin
 * val executor = ContainerCommandExecutor(container)
 *
 * // Execute a simple command
 * val result = executor.executeCommand("echo", "Hello World")
 * println("Output: ${'$'}{result.stdout}")
 *
 * // Execute a command and verify its exit code
 * executor.executeCommandWithExpectedExitCode(0, "ls", "/tmp")
 *
 * // Execute a command and get its output
 * val output = executor.executeCommandAndGetOutput("cat", "/etc/hostname")
 * ```
 *
 * @param container target container to execute commands in
 * @author TrueNine
 * @since 2025-07-12
 */
class ContainerCommandExecutor(private val container: GenericContainer<*>) {

  companion object {
    private val log = LoggerFactory.getLogger(ContainerCommandExecutor::class.java)

    /** Default command execution timeout. */
    val DEFAULT_COMMAND_TIMEOUT: Duration = Duration.ofSeconds(30)

    /** Default max retry attempts. */
    const val DEFAULT_MAX_RETRIES = 3
  }

  /**
   * Executes a command in the container with a retry mechanism.
   *
   * @param timeout command timeout
   * @param maxRetries maximum retry attempts
   * @param commands command and arguments to execute
   * @return command execution result
   * @throws RuntimeException if command execution fails
   */
  fun executeCommand(timeout: Duration = DEFAULT_COMMAND_TIMEOUT, maxRetries: Int = DEFAULT_MAX_RETRIES, vararg commands: String): Container.ExecResult {
    require(commands.isNotEmpty()) { "Commands must not be empty" }

    log.debug("Executing container command: {}", commands.joinToString(" "))

    return TestRetryUtils.retryWithExponentialBackoff(maxAttempts = maxRetries, initialDelay = Duration.ofMillis(100), maxDelay = Duration.ofSeconds(2)) {
      try {
        val result = container.execInContainer(*commands)

        log.debug("Command execution completed, exit code: {}, output length: {}", result.exitCode, result.stdout.length)
        result
      } catch (e: Exception) {
        log.warn("Command execution failed: {}, error: {}", commands.joinToString(" "), e.message)
        throw e
      }
    }
  }

  /**
   * Executes a command and verifies the exit code.
   *
   * @param expectedExitCode expected exit code
   * @param timeout command timeout
   * @param maxRetries maximum retry attempts
   * @param commands command and arguments to execute
   * @return command execution result
   * @throws AssertionError if the exit code does not match
   */
  fun executeCommandWithExpectedExitCode(
    expectedExitCode: Int,
    timeout: Duration = DEFAULT_COMMAND_TIMEOUT,
    maxRetries: Int = DEFAULT_MAX_RETRIES,
    vararg commands: String,
  ): Container.ExecResult {
    val result = executeCommand(timeout, maxRetries, *commands)

    if (result.exitCode != expectedExitCode) {
      val errorMsg =
        "Command exit code mismatch. Expected: $expectedExitCode, actual: ${result.exitCode}, " +
          "command: ${commands.joinToString(" ")}, stdout: ${result.stdout}, stderr: ${result.stderr}"
      log.error(errorMsg)
      throw AssertionError(errorMsg)
    }

    return result
  }

  /**
   * Executes a command and returns standard output.
   *
   * @param timeout command timeout
   * @param maxRetries maximum retry attempts
   * @param commands command and arguments to execute
   * @return standard output content (trimmed)
   */
  fun executeCommandAndGetOutput(timeout: Duration = DEFAULT_COMMAND_TIMEOUT, maxRetries: Int = DEFAULT_MAX_RETRIES, vararg commands: String): String {
    val result = executeCommandWithExpectedExitCode(0, timeout, maxRetries, *commands)
    return result.stdout.trim()
  }

  /**
   * Executes a command and checks whether output contains expected content.
   *
   * @param expectedContent expected content to be contained in output
   * @param timeout command timeout
   * @param maxRetries maximum retry attempts
   * @param commands command and arguments to execute
   * @return command execution result
   * @throws AssertionError if the output does not contain the expected content
   */
  fun executeCommandAndCheckOutput(
    expectedContent: String,
    timeout: Duration = DEFAULT_COMMAND_TIMEOUT,
    maxRetries: Int = DEFAULT_MAX_RETRIES,
    vararg commands: String,
  ): Container.ExecResult {
    val result = executeCommandWithExpectedExitCode(0, timeout, maxRetries, *commands)

    if (!result.stdout.contains(expectedContent)) {
      val errorMsg = "Command output does not contain expected content. Expected: '$expectedContent', " +
        "actual output: '${result.stdout}', command: ${commands.joinToString(" ")}"
      log.error(errorMsg)
      throw AssertionError(errorMsg)
    }

    return result
  }

  /**
   * Waits for the container to be ready (by executing a simple command).
   *
   * @param timeout timeout duration
   * @param pollInterval polling interval
   */
  fun waitForContainerReady(timeout: Duration = Duration.ofSeconds(30), pollInterval: Duration = Duration.ofMillis(500)) {
    log.debug("Waiting for container to be ready")

    TestRetryUtils.waitUntil(timeout = timeout, pollInterval = pollInterval) {
      try {
        // Try to execute a simple command to check whether the container is ready
        val result = container.execInContainer("echo", "ready")
        result.exitCode == 0
      } catch (e: Exception) {
        log.debug("Container is not ready yet: {}", e.message)
        false
      }
    }

    log.debug("Container is ready")
  }

  /**
   * Checks whether a file exists.
   *
   * @param filePath file path
   * @param timeout command timeout
   * @param maxRetries maximum retry attempts
   * @return true if the file exists, false otherwise
   */
  fun fileExists(filePath: String, timeout: Duration = DEFAULT_COMMAND_TIMEOUT, maxRetries: Int = DEFAULT_MAX_RETRIES): Boolean {
    return try {
      val result = executeCommand(timeout, maxRetries, "test", "-f", filePath)
      result.exitCode == 0
    } catch (e: Exception) {
      log.debug("Error while checking file existence: {}", e.message)
      false
    }
  }

  /**
   * Waits for a file to appear.
   *
   * @param filePath file path
   * @param timeout timeout duration
   * @param pollInterval polling interval
   */
  fun waitForFile(filePath: String, timeout: Duration = Duration.ofSeconds(30), pollInterval: Duration = Duration.ofMillis(500)) {
    log.debug("Waiting for file to appear: {}", filePath)

    TestRetryUtils.waitUntil(timeout = timeout, pollInterval = pollInterval) { fileExists(filePath) }

    log.debug("File appeared: {}", filePath)
  }

  /**
   * Reads file content.
   *
   * @param filePath file path
   * @param timeout command timeout
   * @param maxRetries maximum retry attempts
   * @return file content
   */
  fun readFileContent(filePath: String, timeout: Duration = DEFAULT_COMMAND_TIMEOUT, maxRetries: Int = DEFAULT_MAX_RETRIES): String {
    return executeCommandAndGetOutput(timeout, maxRetries, "cat", filePath)
  }
}
