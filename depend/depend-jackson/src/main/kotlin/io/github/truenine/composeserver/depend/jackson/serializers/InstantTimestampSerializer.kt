package io.github.truenine.composeserver.depend.jackson.serializers

import java.time.Instant
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonToken
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.jsontype.TypeSerializer

/**
 * Instant timestamp serializer.
 *
 * Converts Instant to a timestamp in milliseconds.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class InstantTimestampSerializer : ValueSerializer<Instant>() {

  override fun handledType(): Class<Instant> = Instant::class.java

  override fun serialize(value: Instant?, gen: JsonGenerator?, ctxt: SerializationContext?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    val timestamp = value.toEpochMilli()
    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: Instant?, gen: JsonGenerator?, ctxt: SerializationContext?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.typeId(value, shape)
    typeSer?.writeTypePrefix(gen, ctxt, typeIdDef)
    serialize(value, gen, ctxt)
    typeSer?.writeTypeSuffix(gen, ctxt, typeIdDef)
  }
}

/**
 * Instant timestamp deserializer.
 *
 * Supports deserialization from timestamps and multiple string formats into Instant.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class InstantTimestampDeserializer : TimestampDeserializer<Instant>() {

  override fun convertFromTimestamp(timestamp: Long): Instant {
    return Instant.ofEpochMilli(timestamp)
  }

  override fun convertFromString(text: String): Instant {
    return parseWithMultipleFormats(text) ?: throw IllegalArgumentException("Failed to parse Instant string: $text")
  }
}
