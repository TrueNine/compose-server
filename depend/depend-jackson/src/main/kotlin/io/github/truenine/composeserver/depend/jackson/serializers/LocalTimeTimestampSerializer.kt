package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

/**
 * LocalTime 时间戳序列化器
 *
 * 将 LocalTime 转换为 UTC 时间戳（毫秒），结合当前日期
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class LocalTimeTimestampSerializer : JsonSerializer<LocalTime>() {

  override fun handledType(): Class<LocalTime> = LocalTime::class.java

  override fun serialize(value: LocalTime?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    // LocalTime需要结合当前日期转换为时间戳
    val today = LocalDate.now()
    val timestamp = today.atTime(value).toInstant(ZoneOffset.UTC).toEpochMilli()
    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: LocalTime?, gen: JsonGenerator?, serializers: SerializerProvider?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.writeTypePrefix(gen, typeSer.typeId(value, shape))
    serialize(value, gen, serializers)
    typeSer?.writeTypeSuffix(gen, typeIdDef)
  }
}

/**
 * LocalTime 时间戳反序列化器
 *
 * 支持从时间戳和多种字符串格式反序列化为 LocalTime
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class LocalTimeTimestampDeserializer : TimestampDeserializer<LocalTime>() {

  override fun convertFromTimestamp(timestamp: Long): LocalTime {
    return LocalTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
  }

  override fun convertFromString(text: String): LocalTime {
    val instant = parseWithMultipleFormats(text) ?: throw IllegalArgumentException("无法解析 LocalTime 字符串: $text")

    return LocalTime.ofInstant(instant, ZoneOffset.UTC)
  }
}
