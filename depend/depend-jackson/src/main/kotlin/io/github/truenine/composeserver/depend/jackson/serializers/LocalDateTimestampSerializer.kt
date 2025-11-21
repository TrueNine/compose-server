package io.github.truenine.composeserver.depend.jackson.serializers

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonToken
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.jsontype.TypeSerializer

/**
 * LocalDate timestamp serializer.
 *
 * Converts LocalDate to a UTC timestamp in milliseconds using the start of the day.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class LocalDateTimestampSerializer : ValueSerializer<LocalDate>() {

  override fun handledType(): Class<LocalDate> = LocalDate::class.java

  override fun serialize(value: LocalDate?, gen: JsonGenerator?, ctxt: SerializationContext?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    val timestamp = value.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: LocalDate?, gen: JsonGenerator?, ctxt: SerializationContext?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.typeId(value, shape)
    typeSer?.writeTypePrefix(gen, ctxt, typeIdDef)
    serialize(value, gen, ctxt)
    typeSer?.writeTypeSuffix(gen, ctxt, typeIdDef)
  }
}

/**
 * LocalDate timestamp deserializer.
 *
 * Supports deserialization from timestamps and multiple string formats into LocalDate.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class LocalDateTimestampDeserializer : TimestampDeserializer<LocalDate>() {

  override fun convertFromTimestamp(timestamp: Long): LocalDate {
    return LocalDate.ofInstant(java.time.Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
  }

  override fun convertFromString(text: String): LocalDate {
    val instant = parseWithMultipleFormats(text) ?: throw IllegalArgumentException("Failed to parse LocalDate string: $text")

    return LocalDate.ofInstant(instant, ZoneOffset.UTC)
  }
}
