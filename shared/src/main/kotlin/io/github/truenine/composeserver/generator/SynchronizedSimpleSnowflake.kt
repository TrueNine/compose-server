package io.github.truenine.composeserver.generator

/**
 * # Optimized Snowflake ID Generator
 *
 * This is an optimized implementation of the Snowflake algorithm with improved error handling, performance optimizations, and better parameter validation.
 *
 * @param workId Worker machine ID (0-31)
 * @param datacenterId Data center ID (0-31)
 * @param sequence Initial sequence number (default: 0)
 * @param startTimeStamp Start timestamp in milliseconds (default: 2020-01-01 00:00:00 UTC)
 * @param clockBackwardTolerance Clock backward tolerance in milliseconds (default: 5ms)
 * @author TrueNine
 * @since 2023-04-09
 */
open class SynchronizedSimpleSnowflake
@JvmOverloads
constructor(
  private val workId: Long = 0,
  private val datacenterId: Long = 0,
  private var sequence: Long = 0,
  private val startTimeStamp: Long = DEFAULT_START_TIMESTAMP,
  private val clockBackwardTolerance: Long = DEFAULT_CLOCK_BACKWARD_TOLERANCE,
) : ISnowflakeGenerator {

  // Last timestamp, initialized to -1
  private var lastTimestamp = -1L

  // Statistics for monitoring
  private var generatedCount = 0L
  private var sequenceOverflowCount = 0L
  private var clockBackwardCount = 0L

  init {
    validateParameters()
    lastTimestamp = currentTimeMillis()
  }

  private fun validateParameters() {
    require(workId in 0..MAX_WORKER_ID) { "workId must be between 0 and $MAX_WORKER_ID, but got $workId" }
    require(datacenterId in 0..MAX_DATACENTER_ID) { "datacenterId must be between 0 and $MAX_DATACENTER_ID, but got $datacenterId" }
    require(sequence >= 0) { "sequence must be non-negative, but got $sequence" }
    require(startTimeStamp > 0) { "startTimeStamp must be positive, but got $startTimeStamp" }
    require(clockBackwardTolerance >= 0) { "clockBackwardTolerance must be non-negative, but got $clockBackwardTolerance" }

    val currentTime = System.currentTimeMillis()
    require(startTimeStamp <= currentTime) { "startTimeStamp ($startTimeStamp) cannot be in the future (current: $currentTime)" }
  }

  override fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
  }

  @Synchronized
  override fun next(): Long {
    var timestamp = currentTimeMillis()

    // Handle clock backward
    if (timestamp < lastTimestamp) {
      handleClockBackward(timestamp)
      timestamp = currentTimeMillis()
    }

    // Generate multiple IDs within the same millisecond
    if (timestamp == lastTimestamp) {
      sequence = (sequence + 1) and SEQUENCE_MASK
      if (sequence == 0L) {
        sequenceOverflowCount++
        timestamp = waitForNextMillis(lastTimestamp)
      }
    } else {
      sequence = 0
    }

    lastTimestamp = timestamp
    generatedCount++

    // Combine all parts to generate the final ID
    return ((timestamp - startTimeStamp) shl TIMESTAMP_LEFT_SHIFT.toInt()) or
      (datacenterId shl DATACENTER_ID_SHIFT.toInt()) or
      (workId shl WORKER_ID_SHIFT.toInt()) or
      sequence
  }

  private fun handleClockBackward(currentTimestamp: Long) {
    val backwardTime = lastTimestamp - currentTimestamp
    clockBackwardCount++

    if (backwardTime <= clockBackwardTolerance) {
      // For small backward time, wait until time catches up
      try {
        Thread.sleep(backwardTime + 1)
      } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        throw RuntimeException("Interrupted while waiting for clock to catch up", e)
      }
    } else {
      // For large backward time, throw exception
      throw RuntimeException(
        "Clock moved backwards by ${backwardTime}ms, which exceeds tolerance of ${clockBackwardTolerance}ms. " +
          "Current: $currentTimestamp, Last: $lastTimestamp"
      )
    }
  }

  private fun waitForNextMillis(lastTimestamp: Long): Long {
    var timestamp = currentTimeMillis()
    while (timestamp <= lastTimestamp) {
      // Add a small sleep to reduce CPU usage
      try {
        Thread.sleep(1)
      } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        throw RuntimeException("Interrupted while waiting for next millisecond", e)
      }
      timestamp = currentTimeMillis()
    }
    return timestamp
  }

  /** Get generation statistics */
  fun getStatistics(): Statistics {
    return Statistics(generatedCount, sequenceOverflowCount, clockBackwardCount)
  }

  /** Reset statistics counters */
  fun resetStatistics() {
    generatedCount = 0L
    sequenceOverflowCount = 0L
    clockBackwardCount = 0L
  }

  data class Statistics(val generatedCount: Long, val sequenceOverflowCount: Long, val clockBackwardCount: Long)

  companion object {
    // Bit length definitions
    private const val WORKER_ID_BITS = 5L
    private const val DATACENTER_ID_BITS = 5L
    private const val SEQUENCE_BITS = 12L

    // Maximum values calculation
    val MAX_WORKER_ID = (-1L shl WORKER_ID_BITS.toInt()).inv()
    val MAX_DATACENTER_ID = (-1L shl DATACENTER_ID_BITS.toInt()).inv()
    private val SEQUENCE_MASK = (-1L shl SEQUENCE_BITS.toInt()).inv()

    // Bit shift calculations
    private val WORKER_ID_SHIFT = SEQUENCE_BITS
    private val DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS
    private val TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS

    // Default values
    private const val DEFAULT_START_TIMESTAMP = 1577836800000L // 2020-01-01 00:00:00 UTC
    private const val DEFAULT_CLOCK_BACKWARD_TOLERANCE = 5L // 5ms
  }
}
