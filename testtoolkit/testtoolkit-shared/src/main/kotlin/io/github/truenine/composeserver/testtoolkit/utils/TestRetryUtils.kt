package io.github.truenine.composeserver.testtoolkit.utils

import java.time.Duration
import java.time.Instant
import org.slf4j.LoggerFactory

/**
 * Test retry utilities.
 *
 * Provides a unified retry mechanism using Java's built-in waiting and retry strategy. Supports exponential backoff, custom timeouts, and retry conditions.
 *
 * Features:
 * - Exponential backoff retry strategy.
 * - Configurable timeout and polling interval.
 * - Supports custom retry conditions.
 * - Unified exception handling.
 * - Detailed logging.
 *
 * Usage examples:
 * ```kotlin
 * // Simple retry
 * val result = TestRetryUtils.retryUntilSuccess {
 *   someOperationThatMightFail()
 * }
 *
 * // Retry with custom configuration
 * val result = TestRetryUtils.retryUntilSuccess(
 *   timeout = Duration.ofSeconds(30),
 *   pollInterval = Duration.ofMillis(500),
 * ) {
 *   someOperationThatMightFail()
 * }
 *
 * // Wait until a condition is satisfied
 * TestRetryUtils.waitUntil(timeout = Duration.ofSeconds(10)) {
 *   container.isRunning
 * }
 * ```
 *
 * @author TrueNine
 * @since 2025-07-12
 */
object TestRetryUtils {
  private val log = LoggerFactory.getLogger(TestRetryUtils::class.java)

  /** Default timeout. */
  val DEFAULT_TIMEOUT: Duration = Duration.ofSeconds(30)

  /** Default polling interval. */
  val DEFAULT_POLL_INTERVAL: Duration = Duration.ofMillis(200)

  /** Default initial delay. */
  val DEFAULT_INITIAL_DELAY: Duration = Duration.ofMillis(100)

  /**
   * Internal wait implementation.
   *
   * @param timeout overall timeout
   * @param pollInterval polling interval
   * @param initialDelay initial delay before the first check
   * @param condition condition check function
   * @throws RuntimeException when timeout is reached
   */
  private fun waitForCondition(timeout: Duration, pollInterval: Duration, initialDelay: Duration, condition: () -> Boolean) {
    val startTime = Instant.now()
    val endTime = startTime.plus(timeout)

    // Initial delay
    if (!initialDelay.isZero) {
      Thread.sleep(initialDelay.toMillis())
    }

    while (Instant.now().isBefore(endTime)) {
      try {
        if (condition()) {
          return
        }
      } catch (e: Exception) {
        log.debug("Exception occurred while evaluating condition, ignoring: {}", e.message)
      }

      Thread.sleep(pollInterval.toMillis())
    }

    throw RuntimeException("Timed out waiting for condition, timeout: $timeout")
  }

  /**
   * Retries an operation until it succeeds or times out.
   *
   * @param T return type
   * @param timeout overall timeout
   * @param pollInterval polling interval
   * @param initialDelay initial delay before the first attempt
   * @param operation operation to execute
   * @return result of the operation
   * @throws Exception if the operation continues to fail until timeout
   */
  fun <T> retryUntilSuccess(
    timeout: Duration = DEFAULT_TIMEOUT,
    pollInterval: Duration = DEFAULT_POLL_INTERVAL,
    initialDelay: Duration = DEFAULT_INITIAL_DELAY,
    operation: () -> T,
  ): T {
    log.debug("Starting retry operation, timeout: {}, poll interval: {}", timeout, pollInterval)

    val startTime = Instant.now()
    val endTime = startTime.plus(timeout)
    var lastException: Exception? = null

    // Initial delay
    if (!initialDelay.isZero) {
      Thread.sleep(initialDelay.toMillis())
    }

    while (Instant.now().isBefore(endTime)) {
      try {
        val result = operation()
        log.debug("Operation succeeded")
        return result
      } catch (e: Exception) {
        lastException = e
        log.debug("Operation failed, will retry: {}", e.message)
        Thread.sleep(pollInterval.toMillis())
      }
    }

    throw (lastException ?: RuntimeException("Operation failed for unknown reason"))
  }

  /**
   * Waits until a condition is satisfied.
   *
   * @param timeout overall timeout
   * @param pollInterval polling interval
   * @param initialDelay initial delay before the first check
   * @param condition condition to evaluate
   * @throws Exception if the condition is not satisfied within the timeout
   */
  fun waitUntil(
    timeout: Duration = DEFAULT_TIMEOUT,
    pollInterval: Duration = DEFAULT_POLL_INTERVAL,
    initialDelay: Duration = DEFAULT_INITIAL_DELAY,
    condition: () -> Boolean,
  ) {
    log.debug("Waiting for condition, timeout: {}, poll interval: {}", timeout, pollInterval)

    waitForCondition(timeout, pollInterval, initialDelay) {
      val result = condition()
      if (result) {
        log.debug("Condition satisfied")
      } else {
        log.debug("Condition not yet satisfied, continuing to wait")
      }
      result
    }
  }

  /**
   * Waits until a condition based on the supplied result is satisfied and returns the result.
   *
   * @param T return type
   * @param timeout overall timeout
   * @param pollInterval polling interval
   * @param initialDelay initial delay before the first evaluation
   * @param supplier supplier that provides the result
   * @param condition predicate used to check the result
   * @return the result that satisfies the condition
   */
  fun <T> waitUntilResult(
    timeout: Duration = DEFAULT_TIMEOUT,
    pollInterval: Duration = DEFAULT_POLL_INTERVAL,
    initialDelay: Duration = DEFAULT_INITIAL_DELAY,
    supplier: () -> T,
    condition: (T) -> Boolean,
  ): T {
    log.debug("Waiting for result to satisfy condition, timeout: {}, poll interval: {}", timeout, pollInterval)

    val startTime = Instant.now()
    val endTime = startTime.plus(timeout)

    // Initial delay
    if (!initialDelay.isZero) {
      Thread.sleep(initialDelay.toMillis())
    }

    while (Instant.now().isBefore(endTime)) {
      try {
        val result = supplier()
        val satisfied = condition(result)
        if (satisfied) {
          log.debug("Result satisfies condition")
          return result
        } else {
          log.debug("Result does not satisfy condition yet, continuing to wait")
        }
      } catch (e: Exception) {
        log.debug("Exception occurred while obtaining result, ignoring: {}", e.message)
      }

      Thread.sleep(pollInterval.toMillis())
    }

    throw RuntimeException("Timed out waiting for result to satisfy condition, timeout: $timeout")
  }

  /**
   * Retries an operation using an exponential backoff strategy.
   *
   * @param T return type
   * @param maxAttempts maximum number of retry attempts
   * @param initialDelay initial delay before the first retry
   * @param maxDelay maximum delay between retries
   * @param multiplier backoff multiplier
   * @param operation operation to execute
   * @return result of the operation
   * @throws Exception if all retries fail
   */
  fun <T> retryWithExponentialBackoff(
    maxAttempts: Int = 5,
    initialDelay: Duration = Duration.ofMillis(100),
    maxDelay: Duration = Duration.ofSeconds(5),
    multiplier: Double = 2.0,
    operation: () -> T,
  ): T {
    log.debug("Starting exponential backoff retry, max attempts: {}, initial delay: {}", maxAttempts, initialDelay)

    var currentDelay = initialDelay
    var lastException: Exception? = null

    repeat(maxAttempts) { attempt ->
      try {
        log.debug("Attempt {}", attempt + 1)
        return operation()
      } catch (e: Exception) {
        lastException = e
        log.debug("Attempt {} failed: {}", attempt + 1, e.message)

        if (attempt < maxAttempts - 1) {
          log.debug("Waiting {} before next retry", currentDelay)
          Thread.sleep(currentDelay.toMillis())
          currentDelay = Duration.ofMillis(minOf((currentDelay.toMillis() * multiplier).toLong(), maxDelay.toMillis()))
        }
      }
    }

    throw (lastException ?: RuntimeException("All retry attempts failed"))
  }

  /**
   * Waits for a condition using a custom configuration.
   *
   * @param timeout overall timeout
   * @param pollInterval polling interval
   * @param initialDelay initial delay before the first check
   * @param ignoreExceptions whether to ignore exceptions thrown by the condition
   * @param condition condition check function
   */
  fun waitWithCustomConfig(
    timeout: Duration,
    pollInterval: Duration = DEFAULT_POLL_INTERVAL,
    initialDelay: Duration = DEFAULT_INITIAL_DELAY,
    ignoreExceptions: Boolean = true,
    condition: () -> Boolean,
  ) {
    if (ignoreExceptions) {
      waitForCondition(timeout, pollInterval, initialDelay, condition)
    } else {
      // Variant that does not ignore exceptions
      val startTime = Instant.now()
      val endTime = startTime.plus(timeout)

      // Initial delay
      if (!initialDelay.isZero) {
        Thread.sleep(initialDelay.toMillis())
      }

      while (Instant.now().isBefore(endTime)) {
        if (condition()) {
          return
        }
        Thread.sleep(pollInterval.toMillis())
      }

      throw RuntimeException("Timed out waiting for condition, timeout: $timeout")
    }
  }
}
