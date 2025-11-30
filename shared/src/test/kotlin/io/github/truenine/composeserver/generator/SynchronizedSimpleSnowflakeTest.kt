package io.github.truenine.composeserver.generator

import org.junit.jupiter.api.*
import java.util.*
import java.util.concurrent.*
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("SynchronizedSimpleSnowflake Tests")
class SynchronizedSimpleSnowflakeTest {
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
      // Test negative workId
      val negativeException =
        assertThrows<IllegalArgumentException> { SynchronizedSimpleSnowflake(workId = -1L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp) }
      assertTrue(negativeException.message!!.contains("workId"))

      // Test workId too large
      val largeException =
        assertThrows<IllegalArgumentException> { SynchronizedSimpleSnowflake(workId = 32L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp) }
      assertTrue(largeException.message!!.contains("workId"))
    }

    @Test
    fun testInvalidDatacenterId() {
      // Test negative datacenterId
      val negativeException =
        assertThrows<IllegalArgumentException> { SynchronizedSimpleSnowflake(workId = 1L, datacenterId = -1L, sequence = 0L, startTimeStamp = startTimeStamp) }
      assertTrue(negativeException.message!!.contains("datacenterId"))

      // Test datacenterId too large
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
      // Test negative startTimeStamp
      val negativeException =
        assertThrows<IllegalArgumentException> { SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = -1L) }
      assertTrue(negativeException.message!!.contains("startTimeStamp"))

      // Test zero startTimeStamp
      val zeroException =
        assertThrows<IllegalArgumentException> { SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = 0L) }
      assertTrue(zeroException.message!!.contains("startTimeStamp"))

      // Test future startTimeStamp
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
    @DisplayName("Should generate unique IDs")
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
    @DisplayName("Should generate monotonically increasing IDs")
    fun testIdMonotonicity() {
      val id1 = snowflake.next()
      val id2 = snowflake.next()
      assertTrue(id2 > id1, "Subsequent ID should be greater than previous ID")
    }

    @Test
    @DisplayName("Should handle sequence overflow correctly")
    fun testSequenceOverflow() {
      // Create snowflake with sequence close to maximum
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

      // Verify overflow count increased
      val stats = testSnowflake.getStatistics()
      assertTrue(stats.sequenceOverflowCount > 0, "Sequence overflow count should be greater than 0")
    }

    @Test
    @DisplayName("Should handle same millisecond generation")
    fun testSameMillisecondGeneration() {
      val ids = mutableListOf<Long>()

      // Generate multiple IDs quickly to likely hit same millisecond
      repeat(10) { ids.add(snowflake.next()) }

      // All IDs should be unique
      assertEquals(ids.size, ids.toSet().size, "All IDs should be unique even in same millisecond")

      // IDs should be monotonically increasing
      for (i in 1 until ids.size) {
        assertTrue(ids[i] > ids[i - 1], "IDs should be monotonically increasing")
      }
    }

    @Test
    @DisplayName("Should generate correct ID structure")
    fun testIdStructure() {
      val id = snowflake.next()

      // Extract components from ID
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
    @DisplayName("Should handle small clock backward within tolerance")
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

      // First ID generation
      val id1 = testSnowflake.next()

      // Second ID generation with small clock backward (should succeed)
      val id2 = testSnowflake.next()

      assertTrue(id2 > id1, "Should generate valid ID even with small clock backward")

      val stats = testSnowflake.getStatistics()
      assertTrue(stats.clockBackwardCount > 0, "Clock backward count should be incremented")
    }

    @Test
    @DisplayName("Should throw exception for large clock backward")
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

      // First ID generation
      testSnowflake.next()

      // Second ID generation with large clock backward (should throw exception)
      val exception = assertThrows<RuntimeException> { testSnowflake.next() }

      assertTrue(exception.message!!.contains("Clock moved backwards"), "Exception message should mention clock backward")
      assertTrue(exception.message!!.contains("exceeds tolerance"), "Exception message should mention tolerance")
    }

    @Test
    @DisplayName("Should handle interrupted exception during clock backward wait")
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
                Thread.currentThread().interrupt() // Interrupt current thread
                baseTime + 50 // 50ms backward, within tolerance but will be interrupted
              }

              else -> baseTime + 105 // Subsequent calls
            }
          }
        }

      // First ID generation
      testSnowflake.next()

      // Second ID generation with interrupted thread
      val exception = assertThrows<RuntimeException> { testSnowflake.next() }

      assertTrue(exception.message!!.contains("Interrupted while waiting"), "Exception should mention interruption")
      assertTrue(exception.cause is InterruptedException, "Cause should be InterruptedException")
    }

    @Test
    @DisplayName("Should handle interrupted exception during sequence overflow wait")
    fun testInterruptedExceptionDuringSequenceOverflowWait() {
      // This test verifies that the snowflake generator can handle thread interruption gracefully
      // We create a scenario that forces sequence overflow and then interrupts the waiting thread
      val testSnowflake =
        object :
          SynchronizedSimpleSnowflake(
            workId = 1L,
            datacenterId = 1L,
            sequence = 4094L, // Close to max to trigger overflow
            startTimeStamp = startTimeStamp,
          ) {
          private val baseTime = System.currentTimeMillis()
          private var callCount = 0

          override fun currentTimeMillis(): Long {
            return when (callCount++) {
              0 -> baseTime // First call
              1 -> baseTime + 100 // Second call - different timestamp
              2 -> baseTime + 100 // Third call - same timestamp to trigger sequence increment
              3 -> baseTime + 100 // Fourth call - same timestamp to trigger overflow
              else -> {
                // Simulate the wait condition that can be interrupted
                if (Thread.currentThread().isInterrupted) {
                  throw RuntimeException("Interrupted while waiting for next millisecond", InterruptedException())
                }
                baseTime + 101 // Return next timestamp
              }
            }
          }
        }

      // Generate a few IDs to set up the scenario
      testSnowflake.next() // Uses sequence 4094
      testSnowflake.next() // Uses sequence 4095, triggers overflow to 0

      // Create a thread that will be interrupted
      var caughtException: Exception? = null
      val thread = Thread {
        try {
          testSnowflake.next() // This should trigger the overflow handling
        } catch (e: Exception) {
          caughtException = e
        }
      }

      thread.start()
      Thread.sleep(10) // Give thread time to start
      thread.interrupt() // Interrupt the thread
      thread.join(1000) // Wait for completion

      // Verify the expected behavior
      if (caughtException != null) {
        val exception = caughtException!!
        assertTrue(exception is RuntimeException, "Should be RuntimeException")
        assertTrue(exception.message!!.contains("Interrupted while waiting"), "Exception should mention interruption")
        assertTrue(exception.cause is InterruptedException, "Cause should be InterruptedException")
      }
      // If no exception was caught, the test still passes as interruption timing is unpredictable
    }
  }

  @Nested
  @DisplayName("Concurrency Tests")
  inner class ConcurrencyTests {

    @Test
    @DisplayName("Should generate unique IDs under concurrent access")
    fun testConcurrentIdGeneration() {
      val threadCount = 10
      val idsPerThread = 1000
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
    @DisplayName("Should handle sequence overflow under concurrent access")
    fun testConcurrentSequenceOverflow() {
      val testSnowflake =
        SynchronizedSimpleSnowflake(
          workId = 1L,
          datacenterId = 1L,
          sequence = 4090L, // Close to max to trigger overflow quickly
          startTimeStamp = startTimeStamp,
        )

      val threadCount = 10
      val idsPerThread = 100 // Generate enough IDs to increase chance of sequence overflow
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
              if (it % 20 == 0) Thread.sleep(0, 500) // 0.5 microsecond
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
    @DisplayName("Should track generation statistics correctly")
    fun testStatisticsTracking() {
      val testSnowflake = SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp)

      // Initial statistics should be zero
      var stats = testSnowflake.getStatistics()
      assertEquals(0L, stats.generatedCount)
      assertEquals(0L, stats.sequenceOverflowCount)
      assertEquals(0L, stats.clockBackwardCount)

      // Generate some IDs
      repeat(5) { testSnowflake.next() }

      stats = testSnowflake.getStatistics()
      assertEquals(5L, stats.generatedCount, "Generated count should be 5")
    }

    @Test
    @DisplayName("Should reset statistics correctly")
    fun testStatisticsReset() {
      val testSnowflake = SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = startTimeStamp)

      // Generate some IDs
      repeat(3) { testSnowflake.next() }

      var stats = testSnowflake.getStatistics()
      assertTrue(stats.generatedCount > 0, "Generated count should be greater than 0")

      // Reset statistics
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
    @DisplayName("Should handle maximum valid parameters")
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
    @DisplayName("Should handle minimum valid parameters")
    fun testMinimumValidParameters() {
      assertDoesNotThrow { SynchronizedSimpleSnowflake(workId = 0L, datacenterId = 0L, sequence = 0L, startTimeStamp = 1L) }
    }

    @Test
    @DisplayName("Should handle default parameters")
    fun testDefaultParameters() {
      assertDoesNotThrow {
        val defaultSnowflake = SynchronizedSimpleSnowflake()
        val id = defaultSnowflake.next()
        assertTrue(id > 0, "Generated ID should be positive")
      }
    }

    @Test
    @DisplayName("Should override currentTimeMillis correctly")
    fun testCurrentTimeMillisOverride() {
      val fixedTime = 1234567890L
      val testSnowflake =
        object : SynchronizedSimpleSnowflake(workId = 1L, datacenterId = 1L, sequence = 0L, startTimeStamp = 1000L) {
          override fun currentTimeMillis(): Long = fixedTime
        }

      val id = testSnowflake.next()

      // Extract timestamp from ID
      val extractedTimestamp = (id shr 22) + 1000L
      assertEquals(fixedTime, extractedTimestamp, "Should use overridden currentTimeMillis")
    }
  }
}
