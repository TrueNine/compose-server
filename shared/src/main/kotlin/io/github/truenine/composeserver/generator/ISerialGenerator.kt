package io.github.truenine.composeserver.generator

/**
 * ## 序列 生成器
 *
 * @author TrueNine
 * @since 2024-02-28
 */
interface ISerialGenerator<T> {
  fun next(): T

  fun nextString(): String
}
