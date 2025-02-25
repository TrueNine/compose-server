package net.yan100.compose.core.generator

interface IOrderCodeGenerator : ISerialGenerator<Long> {
  override fun next(): Long {
    return nextString().toLong()
  }

  override fun nextString(): String
}
