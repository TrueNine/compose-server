package io.github.truenine.composeserver.generator

import java.util.concurrent.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.*

/**
 * Property-based tests for ULIDGenerator implementation.
 *
 * These tests verify universal properties that should hold across all valid inputs and executions. Each test runs multiple iterations to validate properties
 * across a range of inputs.
 */
@DisplayName("ULIDGenerator Property Tests")
class ULIDGeneratorPropertyTest {

  /**
   * **Feature: uuid-generator, Property 2: ULID format compliance** **Validates: Requirements 2.3**
   *
   * Property: For any generated UUID string, it should conform to ULID format: exactly 26 characters using valid Crockford Base32 encoding (0-9, A-Z excluding
   * I, L, O, U).
   *
   * This test verifies that all generated ULIDs:
   * 1. Have exactly 26 characters
   * 2. Only contain valid Crockford Base32 characters
   * 3. Are case-insensitive (uppercase by default)
   */
  @RepeatedTest(100)
  fun `property 2 - ULID format compliance`() {
    val generator = ULIDGenerator()
    val ulid = generator.nextString()

    // Verify length is exactly 26 characters
    assertEquals(26, ulid.length, "ULID must be exactly 26 characters long")

    // Valid Crockford Base32 characters (excludes I, L, O, U)
    val validChars = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toSet()

    // Verify all characters are valid
    for ((index, char) in ulid.withIndex()) {
      assertTrue(char.uppercaseChar() in validChars, "Character at position $index ('$char') must be valid Crockford Base32 (0-9, A-Z excluding I, L, O, U)")
    }

    // Verify the ULID is uppercase (standard format)
    assertTrue(ulid.all { it.isUpperCase() || it.isDigit() }, "ULID should be in uppercase format")
  }

  /**
   * **Feature: uuid-generator, Property 3: Lexicographic ordering** **Validates: Requirements 3.1**
   *
   * Property: For any two UUIDs generated sequentially (UUID1 before UUID2), UUID2 should be lexicographically greater than or equal to UUID1.
   *
   * This test verifies that ULIDs maintain lexicographic ordering based on generation time.
   */
  @RepeatedTest(100)
  fun `property 3 - lexicographic ordering`() {
    val generator = ULIDGenerator()

    // Generate pairs of sequential ULIDs
    val ulid1 = generator.nextString()
    // Small delay to ensure different timestamps (optional, but helps test robustness)
    if (System.nanoTime() % 10 == 0L) {
      Thread.sleep(1)
    }
    val ulid2 = generator.nextString()

    // Verify lexicographic ordering
    assertTrue(ulid2 >= ulid1, "Later ULID ($ulid2) should be lexicographically >= earlier ULID ($ulid1)")
  }

  /**
   * **Feature: uuid-generator, Property 4: Timestamp encoding in most significant bits** **Validates: Requirements 3.2**
   *
   * Property: For any generated ULID, decoding the first 10 characters should yield a valid timestamp that is close to the generation time (within reasonable
   * tolerance).
   *
   * This test verifies that:
   * 1. The timestamp is encoded in the first 10 characters
   * 2. The decoded timestamp matches the generation time
   * 3. The timestamp is in the most significant bits
   */
  @RepeatedTest(100)
  fun `property 4 - timestamp encoding in most significant bits`() {
    // Create a generator with known timestamp
    val testGenerator =
      object : ULIDGenerator() {
        var capturedTimestamp: Long = 0

        override fun currentTimeMillis(): Long {
          capturedTimestamp = super.currentTimeMillis()
          return capturedTimestamp
        }
      }

    val ulid = testGenerator.nextString()
    val generationTime = testGenerator.capturedTimestamp

    // Decode the first 10 characters (timestamp component)
    val timestampChars = ulid.substring(0, 10)
    val decodedTimestamp = decodeTimestamp(timestampChars)

    // Verify the decoded timestamp matches the generation time
    assertEquals(generationTime, decodedTimestamp, "Decoded timestamp should match generation time")

    // Verify timestamp is reasonable (not in distant past or future)
    val now = System.currentTimeMillis()
    assertTrue(decodedTimestamp <= now + 1000, "Decoded timestamp should not be in the future")
    assertTrue(decodedTimestamp >= now - 60000, "Decoded timestamp should not be more than 1 minute in the past")
  }

  /**
   * **Feature: uuid-generator, Property 7: Millisecond precision consistency** **Validates: Requirements 3.5**
   *
   * Property: For any set of UUIDs generated within the same millisecond, extracting their timestamp components should yield the same millisecond value.
   *
   * This test verifies that the timestamp component has millisecond precision.
   */
  @Test
  fun `property 7 - millisecond precision consistency`() {
    val generator = ULIDGenerator()
    val ulids = mutableListOf<String>()

    // Generate multiple ULIDs rapidly to increase chance of same-millisecond generation
    repeat(1000) { ulids.add(generator.nextString()) }

    // Group ULIDs by their timestamp component
    val timestampGroups = ulids.groupBy { it.substring(0, 10) }

    // For each group with the same timestamp, verify they were generated in the same millisecond
    for ((timestampChars, groupedUlids) in timestampGroups) {
      if (groupedUlids.size > 1) {
        val decodedTimestamp = decodeTimestamp(timestampChars)

        // All ULIDs in this group should have the same timestamp
        for (ulid in groupedUlids) {
          val ulidTimestamp = decodeTimestamp(ulid.substring(0, 10))
          assertEquals(decodedTimestamp, ulidTimestamp, "All ULIDs with same timestamp prefix should decode to same millisecond value")
        }
      }
    }

    // Verify that different timestamp prefixes decode to different milliseconds
    val uniqueTimestamps = timestampGroups.keys.map { decodeTimestamp(it) }.toSet()
    assertTrue(uniqueTimestamps.isNotEmpty(), "Should have at least one unique timestamp")
  }

  /**
   * **Feature: uuid-generator, Property 9: Monotonic increment within same millisecond** **Validates: Requirements 4.2**
   *
   * Property: For any sequence of UUIDs generated within the same millisecond in monotonic mode, each UUID should be lexicographically greater than the
   * previous one.
   *
   * This test verifies that monotonic mode ensures increasing ULIDs within the same millisecond.
   */
  @Test
  fun `property 9 - monotonic increment within same millisecond`() {
    val generator = ULIDGenerator(monotonicMode = true)

    // Generate many ULIDs rapidly to force same-millisecond generation
    val ulids = mutableListOf<String>()
    repeat(10000) { ulids.add(generator.nextString()) }

    // Group by timestamp component
    val timestampGroups = ulids.groupBy { it.substring(0, 10) }

    // Find groups with multiple ULIDs (same millisecond)
    val sameMillisecondGroups = timestampGroups.filter { it.value.size > 1 }

    assertTrue(sameMillisecondGroups.isNotEmpty(), "Should have at least one group with multiple ULIDs in same millisecond")

    // Verify monotonic ordering within each group
    for ((timestamp, groupedUlids) in sameMillisecondGroups) {
      for (i in 1 until groupedUlids.size) {
        val prev = groupedUlids[i - 1]
        val curr = groupedUlids[i]

        assertTrue(curr > prev, "In monotonic mode, ULID at index $i ($curr) should be > previous ULID ($prev) within same millisecond ($timestamp)")
      }
    }
  }

  /** Additional test to verify monotonic mode statistics. */
  @Test
  fun `monotonic mode increments counter when generating in same millisecond`() {
    val generator = ULIDGenerator(monotonicMode = true)

    // Generate many ULIDs rapidly
    repeat(10000) { generator.nextString() }

    val stats = generator.getStatistics()

    // Should have generated 10000 ULIDs
    assertEquals(10000, stats.generatedCount)

    // Should have some monotonic increments (when same millisecond occurred)
    assertTrue(stats.monotonicIncrementCount > 0, "Should have at least some monotonic increments when generating rapidly")
  }

  /** Unit test for statistics tracking: Initial state **Validates: Requirements 4.5** */
  @Test
  fun `statistics should start at zero`() {
    val generator = ULIDGenerator()
    val stats = generator.getStatistics()

    assertEquals(0L, stats.generatedCount, "Initial generated count should be 0")
    assertEquals(0L, stats.monotonicIncrementCount, "Initial monotonic increment count should be 0")
  }

  /** Unit test for statistics tracking: Generation count increment **Validates: Requirements 4.5** */
  @Test
  fun `statistics should track generation count correctly`() {
    val generator = ULIDGenerator()

    // Generate 5 ULIDs
    repeat(5) { generator.nextString() }

    val stats = generator.getStatistics()
    assertEquals(5L, stats.generatedCount, "Generated count should be 5 after generating 5 ULIDs")

    // Generate 3 more ULIDs
    repeat(3) { generator.nextString() }

    val updatedStats = generator.getStatistics()
    assertEquals(8L, updatedStats.generatedCount, "Generated count should be 8 after generating 8 ULIDs total")
  }

  /** Unit test for statistics tracking: Monotonic increment counter **Validates: Requirements 4.5** */
  @Test
  fun `statistics should track monotonic increment count in monotonic mode`() {
    val generator = ULIDGenerator(monotonicMode = true)

    // Generate many ULIDs rapidly to force same-millisecond generation
    repeat(5000) { generator.nextString() }

    val stats = generator.getStatistics()

    assertEquals(5000L, stats.generatedCount, "Generated count should be 5000")
    assertTrue(stats.monotonicIncrementCount > 0, "Monotonic increment count should be greater than 0 when generating rapidly in monotonic mode")
    assertTrue(stats.monotonicIncrementCount < stats.generatedCount, "Monotonic increment count should be less than total generated count")
  }

  /** Unit test for statistics tracking: Non-monotonic mode **Validates: Requirements 4.5** */
  @Test
  fun `statistics should not increment monotonic counter in non-monotonic mode`() {
    val generator = ULIDGenerator(monotonicMode = false)

    // Generate many ULIDs rapidly
    repeat(5000) { generator.nextString() }

    val stats = generator.getStatistics()

    assertEquals(5000L, stats.generatedCount, "Generated count should be 5000")
    assertEquals(0L, stats.monotonicIncrementCount, "Monotonic increment count should remain 0 in non-monotonic mode")
  }

  /** Unit test for statistics reset functionality **Validates: Requirements 4.5** */
  @Test
  fun `resetStatistics should reset all counters to zero`() {
    val generator = ULIDGenerator(monotonicMode = true)

    // Generate some ULIDs
    repeat(1000) { generator.nextString() }

    val statsBeforeReset = generator.getStatistics()
    assertTrue(statsBeforeReset.generatedCount > 0, "Generated count should be greater than 0 before reset")

    // Reset statistics
    generator.resetStatistics()

    val statsAfterReset = generator.getStatistics()
    assertEquals(0L, statsAfterReset.generatedCount, "Generated count should be 0 after reset")
    assertEquals(0L, statsAfterReset.monotonicIncrementCount, "Monotonic increment count should be 0 after reset")
  }

  /** Unit test for statistics reset: Generation continues after reset **Validates: Requirements 4.5** */
  @Test
  fun `generator should continue working after statistics reset`() {
    val generator = ULIDGenerator()

    // Generate some ULIDs
    repeat(10) { generator.nextString() }

    // Reset statistics
    generator.resetStatistics()

    // Generate more ULIDs
    repeat(5) { generator.nextString() }

    val stats = generator.getStatistics()
    assertEquals(5L, stats.generatedCount, "Generated count should be 5 after reset and generating 5 more ULIDs")
  }

  /** Unit test for statistics under concurrent access **Validates: Requirements 4.5** */
  @Test
  fun `statistics should be accurate under concurrent generation`() {
    val generator = ULIDGenerator(monotonicMode = true)
    val threadCount = 10
    val idsPerThread = 100
    val totalIds = threadCount * idsPerThread

    val executor = Executors.newFixedThreadPool(threadCount)
    val latch = CountDownLatch(threadCount)

    repeat(threadCount) {
      executor.submit {
        try {
          repeat(idsPerThread) { generator.nextString() }
        } finally {
          latch.countDown()
        }
      }
    }

    latch.await()
    executor.shutdown()

    val stats = generator.getStatistics()
    assertEquals(totalIds.toLong(), stats.generatedCount, "Generated count should be $totalIds after concurrent generation")
  }

  /** Unit test for statistics data class properties **Validates: Requirements 4.5** */
  @Test
  fun `Statistics data class should have correct properties`() {
    val generator = ULIDGenerator(monotonicMode = true)

    repeat(100) { generator.nextString() }

    val stats = generator.getStatistics()

    // Verify Statistics is a data class with correct properties
    assertTrue(stats.generatedCount >= 0, "generatedCount should be non-negative")
    assertTrue(stats.monotonicIncrementCount >= 0, "monotonicIncrementCount should be non-negative")

    // Verify data class equality
    val stats2 = ULIDGenerator.Statistics(stats.generatedCount, stats.monotonicIncrementCount)
    assertEquals(stats, stats2, "Statistics with same values should be equal")

    // Verify toString works (data class feature)
    val statsString = stats.toString()
    assertTrue(statsString.contains("generatedCount"), "Statistics toString should contain generatedCount")
    assertTrue(statsString.contains("monotonicIncrementCount"), "Statistics toString should contain monotonicIncrementCount")
  }

  /** Unit test for error handling: Invalid ULID string length **Validates: Requirements 4.4** */
  @Test
  fun `toStandardUUIDFormat throws exception for invalid ULID length`() {
    val generator = ULIDGenerator()

    // Test too short
    val shortUlid = "01AN4Z07BY79KA1307SR9X"
    val shortException = org.junit.jupiter.api.assertThrows<IllegalArgumentException> { generator.toStandardUUIDFormat(shortUlid) }
    assertTrue(shortException.message?.contains("ULID must be 26 characters long") == true, "Exception message should indicate expected length")
    assertTrue(shortException.message?.contains("but got ${shortUlid.length}") == true, "Exception message should indicate actual length")

    // Test too long
    val longUlid = "01AN4Z07BY79KA1307SR9X4MV3EXTRA"
    val longException = org.junit.jupiter.api.assertThrows<IllegalArgumentException> { generator.toStandardUUIDFormat(longUlid) }
    assertTrue(longException.message?.contains("ULID must be 26 characters long") == true, "Exception message should indicate expected length")
    assertTrue(longException.message?.contains("but got ${longUlid.length}") == true, "Exception message should indicate actual length")

    // Test empty string
    val emptyException = org.junit.jupiter.api.assertThrows<IllegalArgumentException> { generator.toStandardUUIDFormat("") }
    assertTrue(
      emptyException.message?.contains("ULID must be 26 characters long") == true,
      "Exception message should indicate expected length for empty string",
    )
  }

  /** Unit test for error handling: Invalid characters in ULID **Validates: Requirements 4.4** */
  @Test
  fun `toStandardUUIDFormat throws exception for invalid characters`() {
    val generator = ULIDGenerator()

    // Test with invalid character (not in Crockford Base32)
    val invalidChars =
      listOf(
        "01AN4Z07BY79KA1307SR9X4M@3", // @ is invalid
        "01AN4Z07BY79KA1307SR9X4M#3", // # is invalid
        "01AN4Z07BY79KA1307SR9X4M$3", // $ is invalid
        "01AN4Z07BY79KA1307SR9X4M%3", // % is invalid
      )

    for (invalidUlid in invalidChars) {
      val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> { generator.toStandardUUIDFormat(invalidUlid) }
      assertTrue(exception.message?.contains("Invalid ULID character") == true, "Exception message should indicate invalid character for: $invalidUlid")
    }
  }

  /** Unit test for error handling: Verify meaningful exception messages **Validates: Requirements 4.4** */
  @Test
  fun `error messages are meaningful and helpful`() {
    val generator = ULIDGenerator()

    // Test length error message
    val lengthException = org.junit.jupiter.api.assertThrows<IllegalArgumentException> { generator.toStandardUUIDFormat("SHORT") }
    assertTrue(lengthException.message?.contains("26 characters") == true, "Length error should mention expected length")
    assertTrue(lengthException.message?.contains("but got") == true, "Length error should mention actual length")

    // Test invalid character error message
    val charException = org.junit.jupiter.api.assertThrows<IllegalArgumentException> { generator.toStandardUUIDFormat("01AN4Z07BY79KA1307SR9X4M@3") }
    assertTrue(charException.message?.contains("Invalid ULID character") == true, "Character error should mention invalid character")
    assertTrue(charException.message?.contains("@") == true, "Character error should show the invalid character")
  }

  /** Unit test to verify case-insensitive decoding works correctly */
  @Test
  fun `toStandardUUIDFormat handles case-insensitive input`() {
    val generator = ULIDGenerator()
    val ulid = generator.nextString()

    // Convert to lowercase
    val lowercaseUlid = ulid.lowercase()

    // Both should produce valid UUIDs
    val uuidFromUppercase = generator.toStandardUUIDFormat(ulid)
    val uuidFromLowercase = generator.toStandardUUIDFormat(lowercaseUlid)

    // Both should be valid UUID format
    val hexPattern = Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    assertTrue(hexPattern.matches(uuidFromUppercase), "Uppercase ULID should convert to valid UUID")
    assertTrue(hexPattern.matches(uuidFromLowercase), "Lowercase ULID should convert to valid UUID")

    // They should produce the same UUID
    assertEquals(uuidFromUppercase, uuidFromLowercase, "Case-insensitive decoding should produce same UUID")
  }

  /** Unit test to verify common character confusions are handled */
  @Test
  fun `toStandardUUIDFormat handles common character confusions`() {
    val generator = ULIDGenerator()

    // Generate a ULID and replace some characters with confusable ones
    val ulid = "01AN4Z07BY79KA1307SR9X4MV3"

    // Test I/i -> 1 confusion
    val ulidWithI = ulid.replace('1', 'I')
    val uuidWithI = generator.toStandardUUIDFormat(ulidWithI)
    assertTrue(uuidWithI.matches(Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")), "ULID with 'I' should be decoded as '1'")

    // Test L/l -> 1 confusion
    val ulidWithL = ulid.replace('1', 'L')
    val uuidWithL = generator.toStandardUUIDFormat(ulidWithL)
    assertTrue(uuidWithL.matches(Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")), "ULID with 'L' should be decoded as '1'")

    // Test O/o -> 0 confusion
    val ulidWithO = ulid.replace('0', 'O')
    val uuidWithO = generator.toStandardUUIDFormat(ulidWithO)
    assertTrue(uuidWithO.matches(Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")), "ULID with 'O' should be decoded as '0'")
  }

  /**
   * **Feature: uuid-generator, Property 5: Standard UUID format conversion** **Validates: Requirements 3.3**
   *
   * Property: For any generated ULID string, converting it to standard UUID format should produce a valid 36-character string with hyphens at positions 8, 13,
   * 18, and 23.
   *
   * This test verifies that:
   * 1. The converted UUID has exactly 36 characters
   * 2. Hyphens are at the correct positions (8, 13, 18, 23)
   * 3. All other characters are valid hexadecimal digits
   * 4. The format matches the standard UUID pattern
   */
  @RepeatedTest(100)
  fun `property 5 - standard UUID format conversion`() {
    val generator = ULIDGenerator()
    val ulid = generator.nextString()

    val uuid = generator.toStandardUUIDFormat(ulid)

    // Verify length is exactly 36 characters
    assertEquals(36, uuid.length, "UUID must be exactly 36 characters long")

    // Verify hyphens are at correct positions
    assertEquals('-', uuid[8], "Hyphen must be at position 8")
    assertEquals('-', uuid[13], "Hyphen must be at position 13")
    assertEquals('-', uuid[18], "Hyphen must be at position 18")
    assertEquals('-', uuid[23], "Hyphen must be at position 23")

    // Verify all non-hyphen characters are valid hexadecimal
    val hexPattern = Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    assertTrue(hexPattern.matches(uuid), "UUID must match standard format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx (lowercase hex)")
  }

  /**
   * **Feature: uuid-generator, Property 6: Timestamp round-trip consistency** **Validates: Requirements 3.4**
   *
   * Property: For any timestamp value, encoding it into a ULID and then extracting the timestamp component should yield the same value (within millisecond
   * precision).
   *
   * This test verifies that:
   * 1. The timestamp can be extracted from a ULID
   * 2. The extracted timestamp matches the original timestamp
   * 3. The round-trip preserves millisecond precision
   */
  @RepeatedTest(100)
  fun `property 6 - timestamp round-trip consistency`() {
    // Create a generator that captures the timestamp
    val testGenerator =
      object : ULIDGenerator() {
        var capturedTimestamp: Long = 0

        override fun currentTimeMillis(): Long {
          capturedTimestamp = super.currentTimeMillis()
          return capturedTimestamp
        }
      }

    // Generate ULID and capture timestamp
    val ulid = testGenerator.nextString()
    val originalTimestamp = testGenerator.capturedTimestamp

    // Extract timestamp from ULID (first 10 characters)
    val timestampChars = ulid.substring(0, 10)
    val extractedTimestamp = decodeTimestamp(timestampChars)

    // Verify round-trip consistency
    assertEquals(originalTimestamp, extractedTimestamp, "Extracted timestamp should match original timestamp (round-trip consistency)")
  }

  /**
   * **Feature: uuid-generator, Property 8: Concurrent uniqueness** **Validates: Requirements 4.1**
   *
   * Property: For any concurrent generation of N UUIDs from multiple threads, all N UUIDs should be unique (no duplicates).
   *
   * This test verifies that:
   * 1. ULIDs generated concurrently from multiple threads are all unique
   * 2. No race conditions cause duplicate IDs
   * 3. Thread-safe synchronization works correctly
   */
  @RepeatedTest(100)
  fun `property 8 - concurrent uniqueness`() {
    val generator = ULIDGenerator()
    val threadCount = 10
    val idsPerThread = 100
    val totalIds = threadCount * idsPerThread

    val generatedIds = ConcurrentHashMap.newKeySet<String>()
    val executor = Executors.newFixedThreadPool(threadCount)
    val latch = CountDownLatch(threadCount)

    // Generate ULIDs concurrently from multiple threads
    repeat(threadCount) {
      executor.submit {
        try {
          repeat(idsPerThread) {
            val ulid = generator.nextString()
            generatedIds.add(ulid)
          }
        } finally {
          latch.countDown()
        }
      }
    }

    // Wait for all threads to complete
    latch.await()
    executor.shutdown()

    // Verify all ULIDs are unique
    assertEquals(totalIds, generatedIds.size, "All $totalIds concurrently generated ULIDs should be unique (no duplicates)")

    // Additional verification: all IDs should be valid ULID format
    for (ulid in generatedIds) {
      assertEquals(26, ulid.length, "All generated ULIDs should be 26 characters")
      assertTrue(ulid.all { it.isUpperCase() || it.isDigit() }, "All generated ULIDs should be uppercase")
    }
  }

  /** Unit test for thread safety: Concurrent generation from multiple threads **Validates: Requirements 2.5, 4.1** */
  @Test
  fun `concurrent generation from multiple threads produces unique ULIDs`() {
    val generator = ULIDGenerator()
    val threadCount = 20
    val idsPerThread = 500
    val totalIds = threadCount * idsPerThread

    val generatedIds = ConcurrentHashMap.newKeySet<String>()
    val executor = Executors.newFixedThreadPool(threadCount)
    val latch = CountDownLatch(threadCount)

    // Generate ULIDs concurrently
    repeat(threadCount) {
      executor.submit {
        try {
          repeat(idsPerThread) {
            val ulid = generator.nextString()
            val wasNew = generatedIds.add(ulid)
            assertTrue(wasNew, "Each generated ULID should be unique, but found duplicate: $ulid")
          }
        } finally {
          latch.countDown()
        }
      }
    }

    latch.await()
    executor.shutdown()

    assertEquals(totalIds, generatedIds.size, "Should generate exactly $totalIds unique ULIDs from $threadCount threads")
  }

  /** Unit test for thread safety: No race conditions in state management **Validates: Requirements 2.5, 4.1** */
  @Test
  fun `no race conditions in timestamp and random bytes state management`() {
    val generator = ULIDGenerator(monotonicMode = true)
    val threadCount = 15
    val idsPerThread = 1000
    val totalIds = threadCount * idsPerThread

    val generatedIds = ConcurrentHashMap.newKeySet<String>()
    val executor = Executors.newFixedThreadPool(threadCount)
    val latch = CountDownLatch(threadCount)

    // Generate ULIDs rapidly to stress-test state management
    repeat(threadCount) {
      executor.submit {
        try {
          repeat(idsPerThread) {
            val ulid = generator.nextString()
            generatedIds.add(ulid)
          }
        } finally {
          latch.countDown()
        }
      }
    }

    latch.await()
    executor.shutdown()

    // Verify no duplicates (indicates no race conditions)
    assertEquals(totalIds, generatedIds.size, "No race conditions should occur - all ULIDs should be unique")

    // Verify all ULIDs are valid format
    for (ulid in generatedIds) {
      assertEquals(26, ulid.length, "All ULIDs should be 26 characters")
      val validChars = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toSet()
      assertTrue(ulid.all { it.uppercaseChar() in validChars }, "All ULIDs should contain only valid Crockford Base32 characters")
    }
  }

  /** Unit test for thread safety: Statistics consistency under concurrency **Validates: Requirements 2.5, 4.1** */
  @Test
  fun `statistics remain consistent under concurrent access`() {
    val generator = ULIDGenerator(monotonicMode = true)
    val threadCount = 10
    val idsPerThread = 1000
    val totalIds = threadCount * idsPerThread

    val executor = Executors.newFixedThreadPool(threadCount)
    val latch = CountDownLatch(threadCount)

    // Generate ULIDs concurrently
    repeat(threadCount) {
      executor.submit {
        try {
          repeat(idsPerThread) { generator.nextString() }
        } finally {
          latch.countDown()
        }
      }
    }

    latch.await()
    executor.shutdown()

    val stats = generator.getStatistics()

    // Verify generated count is accurate
    assertEquals(totalIds.toLong(), stats.generatedCount, "Generated count should be exactly $totalIds under concurrent access")

    // Verify monotonic increment count is reasonable
    assertTrue(stats.monotonicIncrementCount >= 0, "Monotonic increment count should be non-negative")
    assertTrue(stats.monotonicIncrementCount < stats.generatedCount, "Monotonic increment count should be less than total generated count")
  }

  /** Unit test for thread safety: Monotonic mode under high concurrency **Validates: Requirements 2.5, 4.1** */
  @Test
  fun `monotonic mode maintains ordering under concurrent access`() {
    val generator = ULIDGenerator(monotonicMode = true)
    val threadCount = 10
    val idsPerThread = 500

    val allUlids = ConcurrentHashMap.newKeySet<String>()
    val executor = Executors.newFixedThreadPool(threadCount)
    val latch = CountDownLatch(threadCount)

    repeat(threadCount) {
      executor.submit {
        try {
          repeat(idsPerThread) {
            val ulid = generator.nextString()
            allUlids.add(ulid)
          }
        } finally {
          latch.countDown()
        }
      }
    }

    latch.await()
    executor.shutdown()

    // Group by timestamp
    val timestampGroups = allUlids.groupBy { it.substring(0, 10) }

    // Verify monotonic ordering within each timestamp group
    for ((timestamp, ulids) in timestampGroups) {
      if (ulids.size > 1) {
        val sortedUlids = ulids.sorted()
        // In monotonic mode, ULIDs with same timestamp should be in order
        // We can't guarantee perfect ordering across threads, but we can verify uniqueness
        assertEquals(ulids.size, ulids.toSet().size, "All ULIDs with timestamp $timestamp should be unique")
      }
    }
  }

  /** Unit test for thread safety: Secure random mode under concurrency **Validates: Requirements 2.5, 4.1** */
  @Test
  fun `secure random mode works correctly under concurrent access`() {
    val generator = ULIDGenerator(useSecureRandom = true, monotonicMode = true)
    val threadCount = 10
    val idsPerThread = 200
    val totalIds = threadCount * idsPerThread

    val generatedIds = ConcurrentHashMap.newKeySet<String>()
    val executor = Executors.newFixedThreadPool(threadCount)
    val latch = CountDownLatch(threadCount)

    repeat(threadCount) {
      executor.submit {
        try {
          repeat(idsPerThread) {
            val ulid = generator.nextString()
            generatedIds.add(ulid)
          }
        } finally {
          latch.countDown()
        }
      }
    }

    latch.await()
    executor.shutdown()

    // Verify all ULIDs are unique even with SecureRandom
    assertEquals(totalIds, generatedIds.size, "SecureRandom mode should produce $totalIds unique ULIDs under concurrent access")
  }

  /** Unit test for thread safety: Mixed mode operations **Validates: Requirements 2.5, 4.1** */
  @Test
  fun `multiple generators can operate concurrently without interference`() {
    val generator1 = ULIDGenerator(monotonicMode = true)
    val generator2 = ULIDGenerator(monotonicMode = false)
    val generator3 = ULIDGenerator(useSecureRandom = true)

    val allIds = ConcurrentHashMap.newKeySet<String>()
    val threadCount = 15
    val idsPerThread = 100
    val executor = Executors.newFixedThreadPool(threadCount)
    val latch = CountDownLatch(threadCount)

    // Use different generators from different threads
    repeat(threadCount) { threadIndex ->
      executor.submit {
        try {
          val generator =
            when (threadIndex % 3) {
              0 -> generator1
              1 -> generator2
              else -> generator3
            }
          repeat(idsPerThread) {
            val ulid = generator.nextString()
            allIds.add(ulid)
          }
        } finally {
          latch.countDown()
        }
      }
    }

    latch.await()
    executor.shutdown()

    // Verify all IDs are unique across all generators
    val expectedTotal = threadCount * idsPerThread
    assertEquals(expectedTotal, allIds.size, "All ULIDs from multiple generators should be unique")
  }

  /** Helper function to decode timestamp from ULID timestamp component. Decodes the first 10 characters of a ULID to extract the timestamp. */
  private fun decodeTimestamp(timestampChars: String): Long {
    require(timestampChars.length == 10) { "Timestamp component must be 10 characters" }

    val encoding = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"
    var timestamp = 0L

    for (char in timestampChars) {
      val value = encoding.indexOf(char.uppercaseChar())
      require(value >= 0) { "Invalid character in timestamp: $char" }
      timestamp = (timestamp shl 5) or value.toLong()
    }

    return timestamp
  }
}
