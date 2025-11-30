package io.github.truenine.composeserver.depend.jackson.serializers

import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonToken
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.jsontype.TypeSerializer
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * OffsetDateTime timestamp serializer.
 *
 * Converts OffsetDateTime to a UTC timestamp in milliseconds.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class OffsetDateTimeTimestampSerializer : ValueSerializer<OffsetDateTime>() {

  override fun handledType(): Class<OffsetDateTime> = OffsetDateTime::class.java

  override fun serialize(value: OffsetDateTime?, gen: JsonGenerator?, ctxt: SerializationContext?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    val timestamp = value.toInstant().toEpochMilli()
    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: OffsetDateTime?, gen: JsonGenerator?, ctxt: SerializationContext?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.typeId(value, shape)
    typeSer?.writeTypePrefix(gen, ctxt, typeIdDef)
    serialize(value, gen, ctxt)
    typeSer?.writeTypeSuffix(gen, ctxt, typeIdDef)
  }
}

/**
 * OffsetDateTime timestamp deserializer.
 *
 * Supports deserialization from timestamps and multiple string formats into OffsetDateTime.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class OffsetDateTimeTimestampDeserializer : TimestampDeserializer<OffsetDateTime>() {

  override fun convertFromTimestamp(timestamp: Long): OffsetDateTime {
    return OffsetDateTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
  }

  override fun convertFromString(text: String): OffsetDateTime {
    val instant = parseWithMultipleFormats(text) ?: throw IllegalArgumentException("Failed to parse OffsetDateTime string: $text")

    return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC)
  }
}
