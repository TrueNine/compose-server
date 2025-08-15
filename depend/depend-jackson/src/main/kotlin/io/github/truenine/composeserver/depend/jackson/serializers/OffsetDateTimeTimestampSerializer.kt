package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * OffsetDateTime 时间戳序列化器
 *
 * 将 OffsetDateTime 转换为 UTC 时间戳（毫秒）
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class OffsetDateTimeTimestampSerializer : JsonSerializer<OffsetDateTime>() {

  override fun handledType(): Class<OffsetDateTime> = OffsetDateTime::class.java

  override fun serialize(value: OffsetDateTime?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    val timestamp = value.toInstant().toEpochMilli()
    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: OffsetDateTime?, gen: JsonGenerator?, serializers: SerializerProvider?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.writeTypePrefix(gen, typeSer.typeId(value, shape))
    serialize(value, gen, serializers)
    typeSer?.writeTypeSuffix(gen, typeIdDef)
  }
}

/**
 * OffsetDateTime 时间戳反序列化器
 *
 * 支持从时间戳和多种字符串格式反序列化为 OffsetDateTime
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class OffsetDateTimeTimestampDeserializer : TimestampDeserializer<OffsetDateTime>() {

  override fun convertFromTimestamp(timestamp: Long): OffsetDateTime {
    return OffsetDateTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
  }

  override fun convertFromString(text: String): OffsetDateTime {
    val instant = parseWithMultipleFormats(text) ?: throw IllegalArgumentException("无法解析 OffsetDateTime 字符串: $text")

    return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC)
  }
}
