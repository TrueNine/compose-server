package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * ZonedDateTime 时间戳序列化器
 *
 * 将 ZonedDateTime 转换为 UTC 时间戳（毫秒）
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class ZonedDateTimeTimestampSerializer : JsonSerializer<ZonedDateTime>() {

  override fun handledType(): Class<ZonedDateTime> = ZonedDateTime::class.java

  override fun serialize(value: ZonedDateTime?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    val timestamp = value.toInstant().toEpochMilli()
    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: ZonedDateTime?, gen: JsonGenerator?, serializers: SerializerProvider?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.writeTypePrefix(gen, typeSer.typeId(value, shape))
    serialize(value, gen, serializers)
    typeSer?.writeTypeSuffix(gen, typeIdDef)
  }
}

/**
 * ZonedDateTime 时间戳反序列化器
 *
 * 支持从时间戳和多种字符串格式反序列化为 ZonedDateTime
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class ZonedDateTimeTimestampDeserializer : TimestampDeserializer<ZonedDateTime>() {

  override fun convertFromTimestamp(timestamp: Long): ZonedDateTime {
    return ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
  }

  override fun convertFromString(text: String): ZonedDateTime {
    val instant = parseWithMultipleFormats(text) ?: throw IllegalArgumentException("无法解析 ZonedDateTime 字符串: $text")

    return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
  }
}
