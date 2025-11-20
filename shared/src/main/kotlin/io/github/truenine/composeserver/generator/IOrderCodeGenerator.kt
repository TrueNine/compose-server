package io.github.truenine.composeserver.generator

/**
 * ## Order code generator.
 * 1. Order codes must not start with 0.
 * 2. All generated order codes can be converted to Long.
 * 3. String representation must be convertible to Long and be >= 1000.
 *
 * @author TrueNine
 * @since 2024-09-15
 */
interface IOrderCodeGenerator : ISerialGenerator<Long> {
  override fun next(): Long {
    return nextString().toLong()
  }

  override fun nextString(): String
}
