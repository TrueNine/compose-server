package io.github.truenine.composeserver.generator

import io.github.truenine.composeserver.datetime
import io.github.truenine.composeserver.logger
import java.time.format.DateTimeFormatter

/**
 * 同步简单订单编号生成器
 *
 * 该生成器组合了时间戳和雪花算法ID来生成唯一的订单号 格式: yyyyMMddHHmmssSSS + snowflakeId
 *
 * @param snowflake 雪花算法生成器，用于生成唯一ID
 * @author TrueNine
 * @since 2024-09-15
 */
class SynchronizedSimpleOrderCodeGenerator(private val snowflake: ISnowflakeGenerator) : IOrderCodeGenerator {
  private val logger = logger<SynchronizedSimpleOrderCodeGenerator>()
  private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")

  @Synchronized
  override fun nextString(): String {
    return try {
      val currentDateTime = datetime.now().format(dateTimeFormatter)
      val snowflakeId = snowflake.nextString()
      val orderCode = buildString {
        append(currentDateTime)
        append(snowflakeId)
      }

      logger.debug("Generated order code: {} with timestamp: {} and snowflake: {}", orderCode, currentDateTime, snowflakeId)

      orderCode
    } catch (exception: Exception) {
      logger.error("Failed to generate order code", exception)
      throw IllegalStateException("Order code generation failed", exception)
    }
  }
}
