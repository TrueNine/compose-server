package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * Unified timestamp serializer
 *
 * Converts all time types to UTC timestamps (milliseconds) to ensure timezone independence and high performance.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class TimestampSerializer : JsonSerializer<Any>() {

  override fun serialize(value: Any?, gen: JsonGenerator?, serializers: SerializerProvider?) {
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

  override fun serializeWithType(value: Any?, gen: JsonGenerator?, serializers: SerializerProvider?, typeSer: TypeSerializer?) {
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer?.writeTypePrefix(gen, typeSer.typeId(value, shape))
    serialize(value, gen, serializers)
    typeSer?.writeTypeSuffix(gen, typeIdDef)
  }
}
