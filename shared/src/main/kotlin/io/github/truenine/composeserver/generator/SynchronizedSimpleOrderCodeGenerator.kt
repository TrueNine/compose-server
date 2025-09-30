package io.github.truenine.composeserver.generator

import io.github.truenine.composeserver.logger

/**
 * 同步简单订单编号生成器
 *
 * 该生成器组合了当前时间戳（毫秒）和雪花算法ID来生成唯一的订单号 格式: currentTimeMillis + snowflakeId
 *
 * @param snowflake 雪花算法生成器，用于生成唯一ID
 * @author TrueNine
 * @since 2024-09-15
 */
class SynchronizedSimpleOrderCodeGenerator(private val snowflake: ISnowflakeGenerator) : IOrderCodeGenerator {
  private val logger = logger<SynchronizedSimpleOrderCodeGenerator>()

  @Synchronized
  override fun nextString(): String {
    return try {
      val timestampMillis = snowflake.currentTimeMillis().coerceAtLeast(0L)
      val snowflakeId = snowflake.nextString()
      val orderCode = buildString {
        append(timestampMillis)
        if (snowflakeId.isNotEmpty()) append(snowflakeId)
      }

      logger.debug("Generated order code: {} with timestamp millis: {} and snowflake: {}", orderCode, timestampMillis, snowflakeId)

      orderCode
    } catch (exception: Exception) {
      logger.error("Failed to generate order code", exception)
      throw IllegalStateException("Order code generation failed", exception)
    }
  }
}
