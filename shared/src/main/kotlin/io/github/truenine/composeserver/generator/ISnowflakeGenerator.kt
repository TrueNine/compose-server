package io.github.truenine.composeserver.generator

interface ISnowflakeGenerator : IOrderCodeGenerator, ISerialGenerator<Long> {
  fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
  }

  override fun next(): Long

  override fun nextString(): String {
    return next().toString()
  }
}
