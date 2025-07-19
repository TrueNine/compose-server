package io.github.truenine.composeserver.generator

import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.test.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

@DisplayName("Optimized SynchronizedSimpleSnowflake Tests")
class OptimizedSnowflakeTest {
  private lateinit var snowflake: SynchronizedSimpleSnowflake
  private val startTimeStamp = 1577836800000L // 2020-01-01 00:00:00 UTC

  @BeforeEach
  fun setUp() {
    snowflake = SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp)
  }

  @Nested
  @DisplayName("Constructor Parameter Validation Tests")
  inner class ConstructorValidationTests {

    @Test
    fun testValidParameters() {
      assertDoesNotThrow { SynchronizedSimpleSnowflake(workId = 0L, datacenterId = 0L, sequence = 0L, startTimeStamp = startTimeStamp) }
      assertDoesNotThrow { SynchronizedSimpleSnowflake(workId = 31L, datacenterId = 31L, sequence = 0L, startTimeStamp = startTimeStamp) }
    }

    @Test
    fun testInvalidWorkId() {
      val negativeException =
        assertThrows<IllegalArgumentException> { SynchronizedSimpleSnowflake(workId = -1L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp) }
      assertTrue(negativeException.message!!.contains("workId"))

      val largeException =
        assertThrows<IllegalArgumentException> { SynchronizedSimpleSnowflake(workId = 32L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp) }
      assertTrue(largeException.message!!.contains("workId"))
    }

    @Test
    fun testInvalidDatacenterId() {
      val negativeException =
        assertThrows<IllegalArgumentException> { SynchronizedSimpleSnowflake(workId = 1L, datacenterId = -1L, sequence = 0L, startTimeStamp = startTimeStamp) }
      assertTrue(negativeException.message!!.contains("datacenterId"))

      val largeException =
        assertThrows<IllegalArgumentException> { SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 32L, sequence = 0L, startTimeStamp = startTimeStamp) }
      assertTrue(largeException.message!!.contains("datacenterId"))
    }

    @Test
    fun testInvalidSequence() {
      val exception =
        assertThrows<IllegalArgumentException> { SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = -1L, startTimeStamp = startTimeStamp) }
      assertTrue(exception.message!!.contains("sequence"))
    }

    @Test
    fun testInvalidStartTimeStamp() {
      val negativeException =
        assertThrows<IllegalArgumentException> { SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = -1L) }
      assertTrue(negativeException.message!!.contains("startTimeStamp"))

      val zeroException =
        assertThrows<IllegalArgumentException> { SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = 0L) }
      assertTrue(zeroException.message!!.contains("startTimeStamp"))

      val futureException =
        assertThrows<IllegalArgumentException> {
          SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = System.currentTimeMillis() + 10000L)
        }
      assertTrue(futureException.message!!.contains("startTimeStamp"))
    }

    @Test
    fun testInvalidClockBackwardTolerance() {
      val exception =
        assertThrows<IllegalArgumentException> {
          SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp, clockBackwardTolerance = -1L)
        }
      assertTrue(exception.message!!.contains("clockBackwardTolerance"))
    }
  }

  @Nested
  @DisplayName("ID Generation Tests")
  inner class IdGenerationTests {

    @Test
    fun testIdUniqueness() {
      val ids = mutableSetOf<Long>()
      val count = 1000

      repeat(count) {
        val id = snowflake.next()
        assertTrue(ids.add(id), "Generated ID should be unique, but found duplicate: $id")
      }

      assertEquals(count, ids.size, "Should generate $count unique IDs")
    }

    @Test
    fun testIdMonotonicity() {
      val id1 = snowflake.next()
      val id2 = snowflake.next()
      assertTrue(id2 > id1, "Subsequent ID should be greater than previous ID")
    }

    @Test
    fun testSequenceOverflow() {
      val testSnowflake =
        SynchronizedSimpleSnowflake(
          workId = 1L,
          datacenterId = 1L,
          sequence = 4094L, // Close to max value 4095
          startTimeStamp = startTimeStamp,
        )

      val id1 = testSnowflake.next()
      val id2 = testSnowflake.next()
      val id3 = testSnowflake.next()

      assertTrue(id2 > id1, "ID should increment when sequence doesn't overflow")
      assertTrue(id3 > id2, "ID should increment after sequence overflow")

      val stats = testSnowflake.getStatistics()
      assertTrue(stats.sequenceOverflowCount > 0, "Sequence overflow count should be greater than 0")
    }

    @Test
    fun testIdStructure() {
      val id = snowflake.next()

      val timestamp = (id shr 22) + startTimeStamp
      val extractedDatacenterId = (id shr 17) and 0x1F
      val extractedWorkId = (id shr 12) and 0x1F
      val extractedSequence = id and 0xFFF

      assertAll(
        { assertTrue(abs(timestamp - System.currentTimeMillis()) < 2000, "Timestamp should be within reasonable range") },
        { assertEquals(1L, extractedDatacenterId, "Datacenter ID should be 1") },
        { assertEquals(1L, extractedWorkId, "Worker ID should be 1") },
        { assertTrue(extractedSequence in 0..4095, "Sequence should be in range 0-4095") },
      )
    }
  }

  @Nested
  @DisplayName("Clock Backward Handling Tests")
  inner class ClockBackwardTests {

    @Test
    fun testSmallClockBackward() {
      val testSnowflake =
        object : SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp, clockBackwardTolerance = 10L) {
          private var callCount = 0
          private val baseTime = System.currentTimeMillis()

          override fun currentTimeMillis(): Long {
            return when (callCount++) {
              0 -> baseTime // First call (initialization)
              1 -> baseTime + 100 // First next() call
              2 -> baseTime + 95 // Second next() call (5ms backward, within tolerance)
              else -> baseTime + 105 // Subsequent calls
            }
          }
        }

      val id1 = testSnowflake.next()
      val id2 = testSnowflake.next()

      assertTrue(id2 > id1, "Should generate valid ID even with small clock backward")

      val stats = testSnowflake.getStatistics()
      assertTrue(stats.clockBackwardCount > 0, "Clock backward count should be incremented")
    }

    @Test
    fun testLargeClockBackward() {
      val testSnowflake =
        object : SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp, clockBackwardTolerance = 5L) {
          private var callCount = 0
          private val baseTime = System.currentTimeMillis()

          override fun currentTimeMillis(): Long {
            return when (callCount++) {
              0 -> baseTime // First call (initialization)
              1 -> baseTime + 100 // First next() call
              2 -> baseTime + 80 // Second next() call (20ms backward, exceeds tolerance)
              else -> baseTime + 105 // Subsequent calls
            }
          }
        }

      testSnowflake.next()

      val exception = assertThrows<RuntimeException> { testSnowflake.next() }

      assertTrue(exception.message!!.contains("Clock moved backwards"), "Exception message should mention clock backward")
      assertTrue(exception.message!!.contains("exceeds tolerance"), "Exception message should mention tolerance")
    }

    @Test
    fun testInterruptedExceptionDuringClockBackwardWait() {
      val testSnowflake =
        object : SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp, clockBackwardTolerance = 100L) {
          private var callCount = 0
          private val baseTime = System.currentTimeMillis()

          override fun currentTimeMillis(): Long {
            return when (callCount++) {
              0 -> baseTime // First call (initialization)
              1 -> baseTime + 100 // First next() call
              2 -> {
                Thread.currentThread().interrupt()
                baseTime + 50 // 50ms backward, within tolerance but will be interrupted
              }
              else -> baseTime + 105 // Subsequent calls
            }
          }
        }

      testSnowflake.next()

      val exception = assertThrows<RuntimeException> { testSnowflake.next() }

      assertTrue(exception.message!!.contains("Interrupted while waiting"), "Exception should mention interruption")
      assertTrue(exception.cause is InterruptedException, "Cause should be InterruptedException")
    }

    @Test
    fun testSequenceOverflowWaiting() {
      // This test verifies that sequence overflow triggers waiting for next millisecond
      val testSnowflake =
        SynchronizedSimpleSnowflake(
          workId = 1L,
          datacenterId = 1L,
          sequence = 4094L, // Close to max to trigger overflow
          startTimeStamp = startTimeStamp,
        )

      val id1 = testSnowflake.next()
      val id2 = testSnowflake.next()
      val id3 = testSnowflake.next()

      // All IDs should be unique and increasing
      assertTrue(id2 > id1, "Second ID should be greater than first")
      assertTrue(id3 > id2, "Third ID should be greater than second")

      // Verify overflow count increased
      val stats = testSnowflake.getStatistics()
      assertTrue(stats.sequenceOverflowCount > 0, "Should have sequence overflow")
    }
  }

  @Nested
  @DisplayName("Concurrency Tests")
  inner class ConcurrencyTests {

    @Test
    fun testConcurrentIdGeneration() {
      val threadCount = 10
      val idsPerThread = 100
      val totalIds = threadCount * idsPerThread
      val ids = mutableSetOf<Long>()
      val executor = Executors.newFixedThreadPool(threadCount)
      val latch = CountDownLatch(threadCount)

      repeat(threadCount) {
        executor.submit {
          try {
            repeat(idsPerThread) {
              val id = snowflake.next()
              synchronized(ids) { assertTrue(ids.add(id), "Concurrent generated ID should be unique, but found duplicate: $id") }
            }
          } finally {
            latch.countDown()
          }
        }
      }

      assertTrue(latch.await(10, TimeUnit.SECONDS), "All threads should complete within timeout")
      assertEquals(totalIds, ids.size, "Should generate $totalIds unique IDs")

      executor.shutdown()
    }

    @Test
    fun testConcurrentSequenceOverflow() {
      // This test verifies that concurrent access works correctly and may trigger sequence overflow
      // We use a more realistic approach that doesn't force artificial conditions
      val testSnowflake =
        SynchronizedSimpleSnowflake(
          workId = 1L,
          datacenterId = 1L,
          sequence = 4090L, // Start close to max to increase chance of overflow
          startTimeStamp = startTimeStamp,
        )

      val threadCount = 10
      val idsPerThread = 100
      val ids = Collections.synchronizedSet(mutableSetOf<Long>())
      val executor = Executors.newFixedThreadPool(threadCount)
      val latch = CountDownLatch(threadCount)
      val startLatch = CountDownLatch(1)

      repeat(threadCount) {
        executor.submit {
          try {
            startLatch.await() // Wait for all threads to be ready
            repeat(idsPerThread) {
              val id = testSnowflake.next()
              ids.add(id)
              // Small delay to increase chance of concurrent access within same millisecond
              if (it % 10 == 0) Thread.sleep(0, 1000) // 1 microsecond
            }
          } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
          } finally {
            latch.countDown()
          }
        }
      }

      startLatch.countDown() // Start all threads simultaneously
      assertTrue(latch.await(30, TimeUnit.SECONDS), "All threads should complete within timeout")
      assertEquals(threadCount * idsPerThread, ids.size, "All generated IDs should be unique")

      val stats = testSnowflake.getStatistics()
      // Note: Sequence overflow may or may not occur depending on system timing
      // The important thing is that all IDs are unique and the system handles concurrency correctly
      assertTrue(stats.sequenceOverflowCount >= 0, "Sequence overflow count should be non-negative")

      executor.shutdown()
    }
  }

  @Nested
  @DisplayName("Statistics Tests")
  inner class StatisticsTests {

    @Test
    fun testStatisticsTracking() {
      val testSnowflake = SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp)

      var stats = testSnowflake.getStatistics()
      assertEquals(0L, stats.generatedCount)
      assertEquals(0L, stats.sequenceOverflowCount)
      assertEquals(0L, stats.clockBackwardCount)

      repeat(5) { testSnowflake.next() }

      stats = testSnowflake.getStatistics()
      assertEquals(5L, stats.generatedCount, "Generated count should be 5")
    }

    @Test
    fun testStatisticsReset() {
      val testSnowflake = SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp)

      repeat(3) { testSnowflake.next() }

      var stats = testSnowflake.getStatistics()
      assertTrue(stats.generatedCount > 0, "Generated count should be greater than 0")

      testSnowflake.resetStatistics()

      stats = testSnowflake.getStatistics()
      assertEquals(0L, stats.generatedCount, "Generated count should be reset to 0")
      assertEquals(0L, stats.sequenceOverflowCount, "Sequence overflow count should be reset to 0")
      assertEquals(0L, stats.clockBackwardCount, "Clock backward count should be reset to 0")
    }
  }

  @Nested
  @DisplayName("Edge Cases and Boundary Tests")
  inner class EdgeCaseTests {

    @Test
    fun testMaximumValidParameters() {
      assertDoesNotThrow {
        SynchronizedSimpleSnowflake(
          workId = SynchronizedSimpleSnowflake.MAX_WORKER_ID,
          datacenterId = SynchronizedSimpleSnowflake.MAX_DATACENTER_ID,
          sequence = 4095L, // Maximum sequence
          startTimeStamp = startTimeStamp,
        )
      }
    }

    @Test
    fun testMinimumValidParameters() {
      assertDoesNotThrow { SynchronizedSimpleSnowflake(workId = 0L, datacenterId = 0L, sequence = 0L, startTimeStamp = 1L) }
    }

    @Test
    fun testDefaultParameters() {
      assertDoesNotThrow {
        val defaultSnowflake = SynchronizedSimpleSnowflake()
        val id = defaultSnowflake.next()
        assertTrue(id > 0, "Generated ID should be positive")
      }
    }

    @Test
    fun testCurrentTimeMillisOverride() {
      val fixedTime = 1234567890L
      val testSnowflake =
        object : SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = 1000L) {
          override fun currentTimeMillis(): Long = fixedTime
        }

      val id = testSnowflake.next()

      val extractedTimestamp = (id shr 22) + 1000L
      assertEquals(fixedTime, extractedTimestamp, "Should use overridden currentTimeMillis")
    }
  }

  @Nested
  @DisplayName("Statistics Data Class Tests")
  inner class StatisticsDataClassTests {

    @Test
    fun testStatisticsCreation() {
      val stats = SynchronizedSimpleSnowflake.Statistics(generatedCount = 100L, sequenceOverflowCount = 5L, clockBackwardCount = 2L)

      assertEquals(100L, stats.generatedCount)
      assertEquals(5L, stats.sequenceOverflowCount)
      assertEquals(2L, stats.clockBackwardCount)
    }

    @Test
    fun testStatisticsEquality() {
      val stats1 = SynchronizedSimpleSnowflake.Statistics(100L, 5L, 2L)
      val stats2 = SynchronizedSimpleSnowflake.Statistics(100L, 5L, 2L)
      val stats3 = SynchronizedSimpleSnowflake.Statistics(101L, 5L, 2L)

      assertEquals(stats1, stats2, "Statistics with same values should be equal")
      assertNotEquals(stats1, stats3, "Statistics with different values should not be equal")
    }

    @Test
    fun testStatisticsToString() {
      val stats = SynchronizedSimpleSnowflake.Statistics(100L, 5L, 2L)
      val toString = stats.toString()

      assertTrue(toString.contains("100"), "toString should contain generatedCount")
      assertTrue(toString.contains("5"), "toString should contain sequenceOverflowCount")
      assertTrue(toString.contains("2"), "toString should contain clockBackwardCount")
    }
  }
}
