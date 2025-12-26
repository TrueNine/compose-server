package io.github.truenine.composeserver.generator

import java.util.*

/**
 * # UUID Generator Interface
 *
 * Defines the contract for generating UUIDs with time-based sorting capabilities. This interface follows the same design pattern as ISnowflakeGenerator.
 *
 * The generator produces UUIDs that are:
 * - Lexicographically sortable by timestamp
 * - Globally unique
 * - Compatible with standard UUID format
 * - Timezone-agnostic (uses UTC/Unix epoch)
 *
 * @author TrueNine
 * @since 2025-01-01
 */
interface IUUIDGenerator : ISerialGenerator<String> {

  /**
   * Get current timestamp in milliseconds (timezone-agnostic). Uses System.currentTimeMillis() which returns UTC epoch time.
   *
   * This method can be overridden for testing purposes to provide deterministic timestamps.
   *
   * @return Current timestamp in milliseconds since Unix epoch (1970-01-01 00:00:00 UTC)
   */
  fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
  }

  /**
   * Generate next UUID as String. The default implementation delegates to nextString().
   *
   * @return UUID string representation
   */
  override fun next(): String {
    return nextString()
  }

  /**
   * Generate next UUID as String. This is the primary generation method that must be implemented.
   *
   * Implementations should generate UUIDs that are:
   * - Unique across all invocations
   * - Lexicographically sortable by generation time
   * - Thread-safe for concurrent access
   *
   * @return UUID string representation
   */
  override fun nextString(): String

  /**
   * Generate next UUID as java.util.UUID object. Converts the string representation to UUID type.
   *
   * This method provides compatibility with code that expects java.util.UUID objects rather than strings.
   *
   * @return UUID object
   */
  fun nextUUID(): UUID {
    return UUID.fromString(toStandardUUIDFormat(nextString()))
  }

  /**
   * Convert ULID string to standard UUID format (with hyphens).
   *
   * ULID format: 26 characters Crockford Base32 (e.g., "01AN4Z07BY79KA1307SR9X4MV3") UUID format: 36 characters with hyphens at positions 8, 13, 18, 23 (e.g.,
   * "0188d4e4-5528-7e8c-b9a0-8c7e8c7e8c7e")
   *
   * This conversion enables interoperability with systems that expect standard UUID format while maintaining the benefits of ULID encoding.
   *
   * @param ulid ULID string (26 characters)
   * @return Standard UUID format string (36 characters with hyphens)
   * @throws IllegalArgumentException if the ULID string is invalid
   */
  fun toStandardUUIDFormat(ulid: String): String
}
