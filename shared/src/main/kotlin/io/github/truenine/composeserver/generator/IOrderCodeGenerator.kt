package io.github.truenine.composeserver.generator

interface IOrderCodeGenerator : ISerialGenerator<Long> {
  override fun next(): Long {
    return nextString().toLong()
  }

  override fun nextString(): String
}
