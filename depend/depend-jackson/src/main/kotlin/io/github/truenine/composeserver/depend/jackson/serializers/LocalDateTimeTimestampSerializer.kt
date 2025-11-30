package io.github.truenine.composeserver.depend.jackson.serializers

import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonToken
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.jsontype.TypeSerializer
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * LocalDateTime timestamp serializer.
 *
 * Converts LocalDateTime to a UTC timestamp in milliseconds.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class LocalDateTimeTimestampSerializer : ValueSerializer<LocalDateTime>() {

  override fun handledType(): Class<LocalDateTime> = LocalDateTime::class.java

  override fun serialize(value: LocalDateTime?, gen: JsonGenerator?, ctxt: SerializationContext?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    val timestamp = value.toInstant(ZoneOffset.UTC).toEpochMilli()
    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: LocalDateTime?, gen: JsonGenerator?, ctxt: SerializationContext?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.typeId(value, shape)
    typeSer?.writeTypePrefix(gen, ctxt, typeIdDef)
    serialize(value, gen, ctxt)
    typeSer?.writeTypeSuffix(gen, ctxt, typeIdDef)
  }
}

/**
 * LocalDateTime timestamp deserializer.
 *
 * Supports deserialization from timestamps and multiple string formats into LocalDateTime.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class LocalDateTimeTimestampDeserializer : TimestampDeserializer<LocalDateTime>() {

  override fun convertFromTimestamp(timestamp: Long): LocalDateTime {
    return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
  }

  override fun convertFromString(text: String): LocalDateTime {
    val instant = parseWithMultipleFormats(text) ?: throw IllegalArgumentException("Failed to parse LocalDateTime string: $text")

    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
  }
}
