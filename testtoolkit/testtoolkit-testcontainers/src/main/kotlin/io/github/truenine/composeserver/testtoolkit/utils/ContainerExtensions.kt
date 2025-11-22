package io.github.truenine.composeserver.testtoolkit.utils

import java.time.Duration
import org.testcontainers.containers.Container
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

/**
 * Container extension functions.
 *
 * Provides convenient extension methods for Testcontainers GenericContainer to simplify container operations and improve test stability.
 *
 * @author TrueNine
 * @since 2025-07-12
 */

/**
 * Creates a command executor for the container.
 *
 * @return ContainerCommandExecutor instance
 */
fun GenericContainer<*>.commandExecutor(): ContainerCommandExecutor {
  return ContainerCommandExecutor(this)
}

/**
 * Executes a container command safely with a retry mechanism.
 *
 * @param timeout command timeout
 * @param maxRetries maximum retry attempts
 * @param commands command and arguments to execute
 * @return command execution result
 */
fun GenericContainer<*>.safeExecInContainer(
  timeout: Duration = ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT,
  maxRetries: Int = ContainerCommandExecutor.DEFAULT_MAX_RETRIES,
  vararg commands: String,
): Container.ExecResult {
  return commandExecutor().executeCommand(timeout, maxRetries, *commands)
}

/**
 * Executes a command and verifies the exit code.
 *
 * @param expectedExitCode expected exit code
 * @param timeout command timeout
 * @param maxRetries maximum retry attempts
 * @param commands command and arguments to execute
 * @return command execution result
 */
fun GenericContainer<*>.execWithExpectedExitCode(
  expectedExitCode: Int,
  timeout: Duration = ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT,
  maxRetries: Int = ContainerCommandExecutor.DEFAULT_MAX_RETRIES,
  vararg commands: String,
): Container.ExecResult {
  return commandExecutor().executeCommandWithExpectedExitCode(expectedExitCode, timeout, maxRetries, *commands)
}

/**
 * Executes a command and returns its output.
 *
 * @param timeout command timeout
 * @param maxRetries maximum retry attempts
 * @param commands command and arguments to execute
 * @return standard output content
 */
fun GenericContainer<*>.execAndGetOutput(
  timeout: Duration = ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT,
  maxRetries: Int = ContainerCommandExecutor.DEFAULT_MAX_RETRIES,
  vararg commands: String,
): String {
  return commandExecutor().executeCommandAndGetOutput(timeout, maxRetries, *commands)
}

/**
 * Executes a command and checks whether output contains the expected content.
 *
 * @param expectedContent expected content to be contained in output
 * @param timeout command timeout
 * @param maxRetries maximum retry attempts
 * @param commands command and arguments to execute
 * @return command execution result
 */
fun GenericContainer<*>.execAndCheckOutput(
  expectedContent: String,
  timeout: Duration = ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT,
  maxRetries: Int = ContainerCommandExecutor.DEFAULT_MAX_RETRIES,
  vararg commands: String,
): Container.ExecResult {
  return commandExecutor().executeCommandAndCheckOutput(expectedContent, timeout, maxRetries, *commands)
}

/**
 * Waits until the container is fully ready.
 *
 * @param timeout timeout duration
 * @param pollInterval polling interval
 */
fun GenericContainer<*>.waitForReady(timeout: Duration = Duration.ofSeconds(30), pollInterval: Duration = Duration.ofMillis(500)) {
  commandExecutor().waitForContainerReady(timeout, pollInterval)
}

/**
 * Checks whether a file exists in the container.
 *
 * @param filePath file path
 * @param timeout command timeout
 * @param maxRetries maximum retry attempts
 * @return true if the file exists, false otherwise
 */
fun GenericContainer<*>.fileExists(
  filePath: String,
  timeout: Duration = ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT,
  maxRetries: Int = ContainerCommandExecutor.DEFAULT_MAX_RETRIES,
): Boolean {
  return commandExecutor().fileExists(filePath, timeout, maxRetries)
}

/**
 * Waits for a file to appear in the container.
 *
 * @param filePath file path
 * @param timeout timeout duration
 * @param pollInterval polling interval
 */
fun GenericContainer<*>.waitForFile(filePath: String, timeout: Duration = Duration.ofSeconds(30), pollInterval: Duration = Duration.ofMillis(500)) {
  commandExecutor().waitForFile(filePath, timeout, pollInterval)
}

/**
 * Reads file content from the container.
 *
 * @param filePath file path
 * @param timeout command timeout
 * @param maxRetries maximum retry attempts
 * @return file content
 */
fun GenericContainer<*>.readFile(
  filePath: String,
  timeout: Duration = ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT,
  maxRetries: Int = ContainerCommandExecutor.DEFAULT_MAX_RETRIES,
): String {
  return commandExecutor().readFileContent(filePath, timeout, maxRetries)
}

/**
 * Configures the container to use a more stable log-based wait strategy.
 *
 * @param logMessage log message to wait for
 * @param times number of times the message should appear
 * @param timeout timeout duration
 * @return configured container
 */
fun <T : GenericContainer<T>> T.withStableWaitStrategy(logMessage: String, times: Int = 1, timeout: Duration = Duration.ofSeconds(60)): T {
  return this.waitingFor(Wait.forLogMessage(logMessage, times).withStartupTimeout(timeout))
}

/**
 * Configures the container to use a health-check based wait strategy.
 *
 * @param healthCheckCommand health-check command to run inside the container
 * @param timeout timeout duration
 * @param interval check interval
 * @return configured container
 */
fun <T : GenericContainer<T>> T.withHealthCheck(
  healthCheckCommand: Array<String>,
  timeout: Duration = Duration.ofSeconds(60),
  interval: Duration = Duration.ofSeconds(5),
): T {
  return this.waitingFor(Wait.forHealthcheck().withStartupTimeout(timeout)).withCommand(*healthCheckCommand)
}

/**
 * Starts the container and waits until it is fully ready.
 *
 * @param readyTimeout readiness timeout duration
 * @param readyPollInterval readiness polling interval
 */
fun GenericContainer<*>.startAndWaitForReady(readyTimeout: Duration = Duration.ofSeconds(30), readyPollInterval: Duration = Duration.ofMillis(500)) {
  start()
  waitForReady(readyTimeout, readyPollInterval)
}
