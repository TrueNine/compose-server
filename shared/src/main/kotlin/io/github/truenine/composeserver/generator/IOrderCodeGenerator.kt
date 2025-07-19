package io.github.truenine.composeserver.generator

/**
 * ## 订单编号 生成器
 * 1. 订单号不得以 0 开头
 * 2. 生成的所有订单号可转换为 Long 类型
 * 3. String 类型可转换为 Long 类型 且 >= 1000
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
