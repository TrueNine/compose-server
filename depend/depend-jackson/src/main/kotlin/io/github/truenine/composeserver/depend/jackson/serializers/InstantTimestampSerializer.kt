package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import java.time.Instant

/**
 * Instant 时间戳序列化器
 *
 * 将 Instant 转换为时间戳（毫秒）
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class InstantTimestampSerializer : JsonSerializer<Instant>() {

  override fun handledType(): Class<Instant> = Instant::class.java

  override fun serialize(value: Instant?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    val timestamp = value.toEpochMilli()
    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: Instant?, gen: JsonGenerator?, serializers: SerializerProvider?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.writeTypePrefix(gen, typeSer.typeId(value, shape))
    serialize(value, gen, serializers)
    typeSer?.writeTypeSuffix(gen, typeIdDef)
  }
}

/**
 * Instant 时间戳反序列化器
 *
 * 支持从时间戳和多种字符串格式反序列化为 Instant
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class InstantTimestampDeserializer : TimestampDeserializer<Instant>() {

  override fun convertFromTimestamp(timestamp: Long): Instant {
    return Instant.ofEpochMilli(timestamp)
  }

  override fun convertFromString(text: String): Instant {
    return parseWithMultipleFormats(text) ?: throw IllegalArgumentException("无法解析 Instant 字符串: $text")
  }
}
