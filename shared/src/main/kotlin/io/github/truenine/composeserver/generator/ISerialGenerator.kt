package io.github.truenine.composeserver.generator

/**
 * ## Sequence number generator.
 *
 * @author TrueNine
 * @since 2024-02-28
 */
interface ISerialGenerator<T> {
  fun next(): T

  fun nextString(): String
}
