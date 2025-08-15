package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * 统一的时间戳反序列化器
 *
 * 支持时间戳和多种格式的反序列化，提供灵活的时间格式兼容性
 *
 * @param T 目标时间类型
 * @author TrueNine
 * @since 2025-01-16
 */
abstract class TimestampDeserializer<T> : JsonDeserializer<T>() {

  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): T? {
    if (p == null) return null

    return when (p.currentToken) {
      JsonToken.VALUE_NUMBER_INT -> {
        // 处理时间戳（毫秒）
        val timestamp = p.longValue
        convertFromTimestamp(timestamp)
      }
      JsonToken.VALUE_STRING -> {
        val text = p.text
        if (text.isNullOrBlank()) return null

        // 尝试解析为时间戳
        text.toLongOrNull()?.let { timestamp ->
          return convertFromTimestamp(timestamp)
        }

        // 尝试解析为ISO8601格式
        try {
          convertFromString(text)
        } catch (e: DateTimeParseException) {
          throw IllegalArgumentException("无法解析时间字符串: $text", e)
        }
      }
      else -> null
    }
  }

  /** 从时间戳转换为目标类型 */
  protected abstract fun convertFromTimestamp(timestamp: Long): T

  /** 从字符串转换为目标类型（支持ISO8601等格式） */
  protected abstract fun convertFromString(text: String): T

  companion object {
    private val ISO_FORMATTERS =
      listOf(
        DateTimeFormatter.ISO_INSTANT,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ISO_ZONED_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ISO_LOCAL_TIME,
      )

    /** 尝试使用多种ISO格式解析字符串 */
    @JvmStatic
    protected fun parseWithMultipleFormats(text: String): Instant? {
      for (formatter in ISO_FORMATTERS) {
        try {
          return when (formatter) {
            DateTimeFormatter.ISO_INSTANT -> Instant.parse(text)
            DateTimeFormatter.ISO_OFFSET_DATE_TIME -> OffsetDateTime.parse(text, formatter).toInstant()
            DateTimeFormatter.ISO_ZONED_DATE_TIME -> ZonedDateTime.parse(text, formatter).toInstant()
            DateTimeFormatter.ISO_LOCAL_DATE_TIME -> LocalDateTime.parse(text, formatter).toInstant(ZoneOffset.UTC)
            DateTimeFormatter.ISO_LOCAL_DATE -> LocalDate.parse(text, formatter).atStartOfDay(ZoneOffset.UTC).toInstant()
            DateTimeFormatter.ISO_LOCAL_TIME -> {
              val time = LocalTime.parse(text, formatter)
              LocalDate.now().atTime(time).toInstant(ZoneOffset.UTC)
            }
            else -> continue
          }
        } catch (e: DateTimeParseException) {
          continue
        }
      }
      return null
    }
  }
}
