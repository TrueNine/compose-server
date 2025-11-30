package io.github.truenine.composeserver.generator

import java.util.*
import kotlin.test.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Property-based tests for IUUIDGenerator interface.
 *
 * These tests verify universal properties that should hold across all implementations and all valid inputs, following the property-based testing methodology.
 */
@DisplayName("IUUIDGenerator Property Tests")
class IUUIDGeneratorPropertyTest {

  /**
   * **Feature: uuid-generator, Property 1: Timezone-agnostic timestamp consistency** **Validates: Requirements 1.4, 2.4**
   *
   * Property: For any system timezone setting, calling currentTimeMillis() should return a timestamp that represents the same absolute point in time (Unix
   * epoch milliseconds).
   *
   * This test verifies that the timestamp is timezone-agnostic by:
   * 1. Capturing timestamps in different timezone contexts
   * 2. Verifying they represent the same absolute time
   * 3. Ensuring the difference is within acceptable tolerance (< 10ms)
   */
  @Test
  fun `property 1 - timezone-agnostic timestamp consistency`() {
    // Create a test implementation of IUUIDGenerator
    val generator =
      object : IUUIDGenerator {
        override fun nextString(): String = "test-uuid"

        override fun toStandardUUIDFormat(ulid: String): String = "00000000-0000-0000-0000-000000000000"
      }

    // Store original timezone
    val originalTimeZone = TimeZone.getDefault()

    try {
      // Test with multiple timezones (100 iterations as per design spec)
      val timezones =
        listOf(
          "UTC",
          "America/New_York",
          "Europe/London",
          "Asia/Tokyo",
          "Australia/Sydney",
          "America/Los_Angeles",
          "Europe/Paris",
          "Asia/Shanghai",
          "America/Chicago",
          "Pacific/Auckland",
        )

      val timestamps = mutableListOf<Long>()

      // Collect timestamps from different timezone contexts
      for (i in 0 until 100) {
        val timezone = timezones[i % timezones.size]
        TimeZone.setDefault(TimeZone.getTimeZone(timezone))

        val timestamp = generator.currentTimeMillis()
        timestamps.add(timestamp)

        // Small delay to ensure we're not just getting the same millisecond
        if (i % 10 == 0) {
          Thread.sleep(1)
        }
      }

      // Verify all timestamps are monotonically increasing or equal
      // (they should increase over time, but timezone changes shouldn't affect this)
      for (i in 1 until timestamps.size) {
        assertTrue(timestamps[i] >= timestamps[i - 1], "Timestamp at index $i (${timestamps[i]}) should be >= previous timestamp (${timestamps[i - 1]})")
      }

      // Verify timestamps are reasonable (within last year and not in future)
      val now = System.currentTimeMillis()
      val oneYearAgo = now - (365L * 24 * 60 * 60 * 1000)

      for ((index, timestamp) in timestamps.withIndex()) {
        assertTrue(timestamp >= oneYearAgo, "Timestamp at index $index should not be older than one year")
        assertTrue(timestamp <= now + 1000, "Timestamp at index $index should not be in the future (with 1s tolerance)")
      }

      // Verify that changing timezone doesn't affect the absolute time value
      // by checking that timestamps collected in different timezones are consistent
      TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
      val utcTimestamp1 = generator.currentTimeMillis()

      TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"))
      val tokyoTimestamp = generator.currentTimeMillis()

      TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
      val utcTimestamp2 = generator.currentTimeMillis()

      // The difference between timestamps should be due to execution time, not timezone
      val timeDiff1 = kotlin.math.abs(tokyoTimestamp - utcTimestamp1)
      val timeDiff2 = kotlin.math.abs(utcTimestamp2 - tokyoTimestamp)

      // Differences should be small (< 10ms) since they're just execution delays
      assertTrue(timeDiff1 < 10, "Time difference between UTC and Tokyo should be < 10ms, but was ${timeDiff1}ms")
      assertTrue(timeDiff2 < 10, "Time difference between Tokyo and UTC should be < 10ms, but was ${timeDiff2}ms")
    } finally {
      // Restore original timezone
      TimeZone.setDefault(originalTimeZone)
    }
  }

  /**
   * Additional test to verify currentTimeMillis() returns Unix epoch time.
   *
   * This test ensures that the timestamp is truly timezone-agnostic by verifying it matches System.currentTimeMillis() which always returns UTC epoch time.
   */
  @Test
  fun `currentTimeMillis returns Unix epoch time regardless of timezone`() {
    val generator =
      object : IUUIDGenerator {
        override fun nextString(): String = "test-uuid"

        override fun toStandardUUIDFormat(ulid: String): String = "00000000-0000-0000-0000-000000000000"
      }

    val originalTimeZone = TimeZone.getDefault()

    try {
      val timezones = listOf("UTC", "America/New_York", "Asia/Tokyo", "Europe/London", "Australia/Sydney")

      for (timezone in timezones) {
        TimeZone.setDefault(TimeZone.getTimeZone(timezone))

        val generatorTime = generator.currentTimeMillis()
        val systemTime = System.currentTimeMillis()

        // The difference should be negligible (< 1ms) since both should return the same epoch time
        val diff = kotlin.math.abs(generatorTime - systemTime)
        assertTrue(diff < 1, "In timezone $timezone, generator time should match system time (diff: ${diff}ms)")
      }
    } finally {
      TimeZone.setDefault(originalTimeZone)
    }
  }

  /** Test that verifies the default implementation of currentTimeMillis() delegates to System.currentTimeMillis(). */
  @Test
  fun `currentTimeMillis default implementation uses System currentTimeMillis`() {
    val generator =
      object : IUUIDGenerator {
        override fun nextString(): String = "test-uuid"

        override fun toStandardUUIDFormat(ulid: String): String = "00000000-0000-0000-0000-000000000000"
      }

    // Capture timestamps in quick succession
    val before = System.currentTimeMillis()
    val generatorTime = generator.currentTimeMillis()
    val after = System.currentTimeMillis()

    // Generator time should be between before and after
    assertTrue(generatorTime >= before, "Generator time should be >= time before call")
    assertTrue(generatorTime <= after, "Generator time should be <= time after call")
  }
}
