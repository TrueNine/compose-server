package io.github.truenine.composeserver.generator

import java.security.SecureRandom

/**
 * # ULID Generator Implementation
 *
 * Thread-safe implementation of ULID (Universally Unique Lexicographically Sortable Identifier). ULID combines timestamp and randomness to create sortable
 * unique identifiers.
 *
 * ## ULID Structure
 * - 48 bits: Timestamp (milliseconds since Unix epoch)
 * - 80 bits: Randomness
 * - Total: 128 bits (compatible with UUID)
 *
 * ## String Representation
 * - 26 characters in Crockford Base32 encoding
 * - First 10 characters: Timestamp
 * - Last 16 characters: Randomness
 *
 * ## Features
 * - Lexicographically sortable by timestamp
 * - Monotonic within same millisecond (when enabled)
 * - Thread-safe for concurrent access
 * - URL-safe (no special characters)
 * - Case-insensitive
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Create generator with default settings
 * val generator = ULIDGenerator()
 * val ulid = generator.nextString()
 * // Output: "01AN4Z07BY79KA1307SR9X4MV3"
 *
 * // Create generator with secure random
 * val secureGenerator = ULIDGenerator(useSecureRandom = true)
 *
 * // Create generator without monotonic mode
 * val nonMonotonicGenerator = ULIDGenerator(monotonicMode = false)
 * ```
 *
 * @param useSecureRandom Whether to use SecureRandom for cryptographic strength (default: false). Set to true for security-sensitive applications where
 *   unpredictability is critical. Note: SecureRandom has performance overhead compared to standard Random.
 * @param monotonicMode Whether to use monotonic mode for same-millisecond generation (default: true). When enabled, ULIDs generated within the same millisecond
 *   will be monotonically increasing. When disabled, each ULID uses fresh random bytes regardless of timestamp.
 * @author TrueNine
 * @since 2025-01-01
 */
open class ULIDGenerator @JvmOverloads constructor(private val useSecureRandom: Boolean = false, private val monotonicMode: Boolean = true) : IUUIDGenerator {

  private val random = if (useSecureRandom) SecureRandom() else java.util.Random()

  private var lastTimestamp = -1L
  private var lastRandomBytes = ByteArray(10)

  private var generatedCount = 0L
  private var monotonicIncrementCount = 0L

  init {
    validateParameters()
  }

  private fun validateParameters() {
    // No specific validation needed for current parameters
    // This method is kept for consistency with SynchronizedSimpleSnowflake
    // and for future extensibility if additional parameters are added
  }

  @Synchronized
  override fun nextString(): String {
    val timestamp = currentTimeMillis()

    val randomBytes =
      if (monotonicMode && timestamp == lastTimestamp) {
        monotonicIncrementCount++
        incrementRandomBytes(lastRandomBytes.copyOf())
      } else {
        generateRandomBytes()
      }

    lastTimestamp = timestamp
    lastRandomBytes = randomBytes.copyOf()
    generatedCount++

    return encodeULID(timestamp, randomBytes)
  }

  override fun toStandardUUIDFormat(ulid: String): String {
    require(ulid.length == 26) { "ULID must be 26 characters long, but got ${ulid.length}" }

    val bytes = decodeULID(ulid)

    // Convert bytes to UUID format: 8-4-4-4-12 (36 characters with hyphens)
    // UUID format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
    val hex = bytes.joinToString("") { "%02x".format(it) }

    return buildString {
      append(hex.substring(0, 8))
      append('-')
      append(hex.substring(8, 12))
      append('-')
      append(hex.substring(12, 16))
      append('-')
      append(hex.substring(16, 20))
      append('-')
      append(hex.substring(20, 32))
    }
  }

  private fun generateRandomBytes(): ByteArray {
    val bytes = ByteArray(10)
    random.nextBytes(bytes)
    return bytes
  }

  private fun incrementRandomBytes(bytes: ByteArray): ByteArray {
    // Increment the random bytes as an unsigned integer
    // Start from the least significant byte (rightmost)
    var carry = 1
    for (i in bytes.indices.reversed()) {
      val value = (bytes[i].toInt() and 0xFF) + carry
      bytes[i] = value.toByte()
      carry = value shr 8
      if (carry == 0) break
    }
    // If carry is still 1, we've overflowed - generate new random bytes
    if (carry == 1) {
      random.nextBytes(bytes)
    }
    return bytes
  }

  private fun encodeULID(timestamp: Long, randomBytes: ByteArray): String {
    val chars = CharArray(26)

    // Encode timestamp (10 characters)
    var time = timestamp
    for (i in 9 downTo 0) {
      chars[i] = ENCODING[(time and 0x1F).toInt()]
      time = time shr 5
    }

    // Encode random bytes (16 characters)
    var randomBits = 0L
    var bitsRemaining = 0
    var charIndex = 10

    for (byte in randomBytes) {
      randomBits = (randomBits shl 8) or (byte.toLong() and 0xFF)
      bitsRemaining += 8

      while (bitsRemaining >= 5) {
        bitsRemaining -= 5
        chars[charIndex++] = ENCODING[((randomBits shr bitsRemaining) and 0x1F).toInt()]
      }
    }

    if (bitsRemaining > 0) {
      chars[charIndex] = ENCODING[((randomBits shl (5 - bitsRemaining)) and 0x1F).toInt()]
    }

    return String(chars)
  }

  private fun decodeULID(ulid: String): ByteArray {
    val bytes = ByteArray(16)
    var bits = 0L
    var bitsCount = 0
    var byteIndex = 0

    for (char in ulid.uppercase()) {
      val value = DECODING[char.code]
      require(value >= 0) { "Invalid ULID character: $char" }

      bits = (bits shl 5) or value.toLong()
      bitsCount += 5

      if (bitsCount >= 8) {
        bitsCount -= 8
        bytes[byteIndex++] = ((bits shr bitsCount) and 0xFF).toByte()
      }
    }

    return bytes
  }

  /**
   * Get generation statistics.
   *
   * Provides monitoring information about the generator's operation:
   * - generatedCount: Total number of ULIDs generated
   * - monotonicIncrementCount: Number of times monotonic increment was used
   *
   * @return Statistics object containing generation metrics
   */
  @Synchronized
  fun getStatistics(): Statistics {
    return Statistics(generatedCount, monotonicIncrementCount)
  }

  /**
   * Reset statistics counters.
   *
   * Resets all internal statistics counters to zero. This does not affect the generator's state or ability to generate ULIDs.
   */
  @Synchronized
  fun resetStatistics() {
    generatedCount = 0L
    monotonicIncrementCount = 0L
  }

  /**
   * Statistics data class for monitoring generator performance.
   *
   * @property generatedCount Total number of ULIDs generated since creation or last reset
   * @property monotonicIncrementCount Number of times monotonic increment was used (same millisecond generation)
   */
  data class Statistics(val generatedCount: Long, val monotonicIncrementCount: Long)

  companion object {
    // Crockford Base32 encoding (excludes I, L, O, U to avoid confusion)
    private val ENCODING = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray()

    // Decoding table for Crockford Base32
    private val DECODING =
      IntArray(128) { -1 }
        .apply {
          for (i in ENCODING.indices) {
            this[ENCODING[i].code] = i
            // Support lowercase
            this[ENCODING[i].lowercaseChar().code] = i
          }
          // Handle common confusions
          this['I'.code] = 1
          this['i'.code] = 1
          this['L'.code] = 1
          this['l'.code] = 1
          this['O'.code] = 0
          this['o'.code] = 0
        }
  }
}
