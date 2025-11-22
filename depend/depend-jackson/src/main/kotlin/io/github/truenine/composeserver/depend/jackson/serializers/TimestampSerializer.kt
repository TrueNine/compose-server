package io.github.truenine.composeserver.depend.jackson.serializers

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonToken
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.jsontype.TypeSerializer

/**
 * Unified timestamp serializer
 *
 * Converts all time types to UTC timestamps (milliseconds) to ensure timezone independence and high performance.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class TimestampSerializer : ValueSerializer<Any>() {

  override fun serialize(value: Any?, gen: JsonGenerator?, ctxt: SerializationContext?) {
    if (value == null) {
      gen?.writeNull()
      return
    }

    val timestamp =
      when (value) {
        is LocalDateTime -> value.toInstant(ZoneOffset.UTC).toEpochMilli()
        is LocalDate -> value.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        is LocalTime -> {
          // LocalTime needs to be combined with the current date to be converted to a timestamp
          val today = LocalDate.now()
          today.atTime(value).toInstant(ZoneOffset.UTC).toEpochMilli()
        }

        is Instant -> value.toEpochMilli()
        is ZonedDateTime -> value.toInstant().toEpochMilli()
        is OffsetDateTime -> value.toInstant().toEpochMilli()
        else -> throw IllegalArgumentException("Unsupported time type: ${value::class.java}")
      }

    gen?.writeNumber(timestamp)
  }

  override fun serializeWithType(value: Any?, gen: JsonGenerator?, ctxt: SerializationContext?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.typeId(value, shape)
    typeSer?.writeTypePrefix(gen, ctxt, typeIdDef)
    serialize(value, gen, ctxt)
    typeSer?.writeTypeSuffix(gen, ctxt, typeIdDef)
  }
}
