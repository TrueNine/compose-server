package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * LocalDateTime 时间戳序列化器
 *
 * 将 LocalDateTime 转换为 UTC 时间戳（毫秒）
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class LocalDateTimeTimestampSerializer : JsonSerializer<LocalDateTime>() {

  override fun handledType(): Class<LocalDateTime> = LocalDateTime::class.java

  override fun serialize(value: LocalDateTime?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    val timestamp = value.toInstant(ZoneOffset.UTC).toEpochMilli()
    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: LocalDateTime?, gen: JsonGenerator?, serializers: SerializerProvider?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.writeTypePrefix(gen, typeSer.typeId(value, shape))
    serialize(value, gen, serializers)
    typeSer?.writeTypeSuffix(gen, typeIdDef)
  }
}

/**
 * LocalDateTime 时间戳反序列化器
 *
 * 支持从时间戳和多种字符串格式反序列化为 LocalDateTime
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class LocalDateTimeTimestampDeserializer : TimestampDeserializer<LocalDateTime>() {

  override fun convertFromTimestamp(timestamp: Long): LocalDateTime {
    return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
  }

  override fun convertFromString(text: String): LocalDateTime {
    val instant = parseWithMultipleFormats(text) ?: throw IllegalArgumentException("无法解析 LocalDateTime 字符串: $text")

    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
  }
}
