package io.github.truenine.composeserver.depend.jackson.serializers

import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonToken
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.jsontype.TypeSerializer
import java.time.*

/**
 * LocalTime timestamp serializer.
 *
 * Converts LocalTime to a UTC timestamp in milliseconds, combined with the current date.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class LocalTimeTimestampSerializer : ValueSerializer<LocalTime>() {

  override fun handledType(): Class<LocalTime> = LocalTime::class.java

  override fun serialize(value: LocalTime?, gen: JsonGenerator?, ctxt: SerializationContext?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    // LocalTime needs to be combined with the current date to convert to a timestamp
    val today = LocalDate.now()
    val timestamp = today.atTime(value).toInstant(ZoneOffset.UTC).toEpochMilli()
    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: LocalTime?, gen: JsonGenerator?, ctxt: SerializationContext?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.typeId(value, shape)
    typeSer?.writeTypePrefix(gen, ctxt, typeIdDef)
    serialize(value, gen, ctxt)
    typeSer?.writeTypeSuffix(gen, ctxt, typeIdDef)
  }
}

/**
 * LocalTime timestamp deserializer.
 *
 * Supports deserialization from timestamps and multiple string formats into LocalTime.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class LocalTimeTimestampDeserializer : TimestampDeserializer<LocalTime>() {

  override fun convertFromTimestamp(timestamp: Long): LocalTime {
    return LocalTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
  }

  override fun convertFromString(text: String): LocalTime {
    val instant = parseWithMultipleFormats(text) ?: throw IllegalArgumentException("Failed to parse LocalTime string: $text")

    return LocalTime.ofInstant(instant, ZoneOffset.UTC)
  }
}
