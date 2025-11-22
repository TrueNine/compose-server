package io.github.truenine.composeserver.depend.jackson.serializers

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonToken
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.jsontype.TypeSerializer

/**
 * ZonedDateTime timestamp serializer.
 *
 * Converts ZonedDateTime to a UTC timestamp in milliseconds.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class ZonedDateTimeTimestampSerializer : ValueSerializer<ZonedDateTime>() {

  override fun handledType(): Class<ZonedDateTime> = ZonedDateTime::class.java

  override fun serialize(value: ZonedDateTime?, gen: JsonGenerator?, ctxt: SerializationContext?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    val timestamp = value.toInstant().toEpochMilli()
    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: ZonedDateTime?, gen: JsonGenerator?, ctxt: SerializationContext?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.typeId(value, shape)
    typeSer?.writeTypePrefix(gen, ctxt, typeIdDef)
    serialize(value, gen, ctxt)
    typeSer?.writeTypeSuffix(gen, ctxt, typeIdDef)
  }
}

/**
 * ZonedDateTime timestamp deserializer.
 *
 * Supports deserialization from timestamps and multiple string formats into ZonedDateTime.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class ZonedDateTimeTimestampDeserializer : TimestampDeserializer<ZonedDateTime>() {

  override fun convertFromTimestamp(timestamp: Long): ZonedDateTime {
    return ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
  }

  override fun convertFromString(text: String): ZonedDateTime {
    val instant = parseWithMultipleFormats(text) ?: throw IllegalArgumentException("Failed to parse ZonedDateTime string: $text")

    return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
  }
}
