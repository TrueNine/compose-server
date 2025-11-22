package io.github.truenine.composeserver.testtoolkit.utils

import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * TestRetryUtils test suite.
 *
 * Verifies all behaviors of TestRetryUtils.
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class TestRetryUtilsTest {

  @Test
  fun retryUntilSuccessShouldReturnResultImmediately() {
    val result = TestRetryUtils.retryUntilSuccess { "Success" }
    assertEquals("Success", result)
  }

  @Test
  fun retryUntilSuccessShouldRetryOnFailure() {
    val attemptCounter = AtomicInteger(0)
    val result =
      TestRetryUtils.retryUntilSuccess(timeout = Duration.ofSeconds(5), pollInterval = Duration.ofMillis(100)) {
        val attempt = attemptCounter.incrementAndGet()
        if (attempt < 3) {
          throw RuntimeException("Attempt $attempt failed")
        }
        "Success after $attempt attempts"
      }

    assertTrue(result.contains("Success"))
    assertTrue(attemptCounter.get() >= 3)
  }

  @Test
  fun retryUntilSuccessShouldThrowOnTimeout() {
    assertFailsWith<RuntimeException> {
      TestRetryUtils.retryUntilSuccess(timeout = Duration.ofMillis(500), pollInterval = Duration.ofMillis(100)) { throw RuntimeException("Always fails") }
    }
  }

  @Test
  fun retryWithExponentialBackoffShouldReturnResultImmediately() {
    val result =
      TestRetryUtils.retryWithExponentialBackoff(maxAttempts = 3, initialDelay = Duration.ofMillis(50), maxDelay = Duration.ofSeconds(1)) {
        "Exponential Success"
      }
    assertEquals("Exponential Success", result)
  }

  @Test
  fun retryWithExponentialBackoffShouldRetryOnFailure() {
    val attemptCounter = AtomicInteger(0)
    val result =
      TestRetryUtils.retryWithExponentialBackoff(maxAttempts = 4, initialDelay = Duration.ofMillis(10), maxDelay = Duration.ofMillis(100)) {
        val attempt = attemptCounter.incrementAndGet()
        if (attempt < 3) {
          throw RuntimeException("Attempt $attempt failed")
        }
        "Success after $attempt attempts"
      }

    assertTrue(result.contains("Success"))
    assertTrue(attemptCounter.get() >= 3)
  }

  @Test
  fun retryWithExponentialBackoffShouldRespectMaxAttempts() {
    val attemptCounter = AtomicInteger(0)
    assertFailsWith<RuntimeException> {
      TestRetryUtils.retryWithExponentialBackoff(maxAttempts = 2, initialDelay = Duration.ofMillis(10), maxDelay = Duration.ofMillis(50)) {
        attemptCounter.incrementAndGet()
        throw RuntimeException("Always fails")
      }
    }
    assertEquals(2, attemptCounter.get())
  }

  @Test
  fun waitUntilShouldReturnImmediatelyWhenConditionMet() {
    TestRetryUtils.waitUntil(timeout = Duration.ofSeconds(5), pollInterval = Duration.ofMillis(100)) { true }
  }

  @Test
  fun waitUntilShouldPollConditionUntilSuccess() {
    val attemptCounter = AtomicInteger(0)
    TestRetryUtils.waitUntil(timeout = Duration.ofSeconds(5), pollInterval = Duration.ofMillis(100)) {
      val attempt = attemptCounter.incrementAndGet()
      attempt >= 3
    }
    assertTrue(attemptCounter.get() >= 3)
  }

  @Test
  fun waitUntilShouldThrowOnTimeout() {
    assertFailsWith<RuntimeException> { TestRetryUtils.waitUntil(timeout = Duration.ofMillis(300), pollInterval = Duration.ofMillis(100)) { false } }
  }
}
