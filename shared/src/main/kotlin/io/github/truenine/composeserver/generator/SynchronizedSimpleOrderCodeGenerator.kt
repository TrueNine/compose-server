package io.github.truenine.composeserver.generator

import io.github.truenine.composeserver.logger

/**
 * Synchronized simple order code generator
 *
 * This generator combines current timestamp (milliseconds) and snowflake ID to generate unique order numbers.
 * Format: currentTimeMillis + snowflakeId
 *
 * @param snowflake Snowflake algorithm generator for generating unique IDs
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
