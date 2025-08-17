package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * LocalDate 时间戳序列化器
 *
 * 将 LocalDate 转换为 UTC 时间戳（毫秒），使用当天开始时间
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class LocalDateTimestampSerializer : JsonSerializer<LocalDate>() {

  override fun handledType(): Class<LocalDate> = LocalDate::class.java

  override fun serialize(value: LocalDate?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    val timestamp = value.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: LocalDate?, gen: JsonGenerator?, serializers: SerializerProvider?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.writeTypePrefix(gen, typeSer.typeId(value, shape))
    serialize(value, gen, serializers)
    typeSer?.writeTypeSuffix(gen, typeIdDef)
  }
}

/**
 * LocalDate 时间戳反序列化器
 *
 * 支持从时间戳和多种字符串格式反序列化为 LocalDate
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class LocalDateTimestampDeserializer : TimestampDeserializer<LocalDate>() {

  override fun convertFromTimestamp(timestamp: Long): LocalDate {
    return LocalDate.ofInstant(java.time.Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
  }

  override fun convertFromString(text: String): LocalDate {
    val instant = parseWithMultipleFormats(text) ?: throw IllegalArgumentException("无法解析 LocalDate 字符串: $text")

    return LocalDate.ofInstant(instant, ZoneOffset.UTC)
  }
}
