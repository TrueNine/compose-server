package io.github.truenine.composeserver.depend.jackson.serializers

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import tools.jackson.core.JsonParser
import tools.jackson.core.JsonToken
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.ValueDeserializer

/**
 * Unified timestamp deserializer
 *
 * Supports deserialization from timestamps and multiple formats, providing flexible time format compatibility.
 *
 * @param T Target time type
 * @author TrueNine
 * @since 2025-01-16
 */
abstract class TimestampDeserializer<T> : ValueDeserializer<T>() {

  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): T? {
    if (p == null) return null

    return when (p.currentToken()) {
      JsonToken.VALUE_NUMBER_INT -> {
        // Handle timestamp (milliseconds)
        val timestamp = p.longValue
        convertFromTimestamp(timestamp)
      }

      JsonToken.VALUE_STRING -> {
        val text = p.text
        if (text.isNullOrBlank()) return null

        // Try to parse as a timestamp
        text.toLongOrNull()?.let { timestamp ->
          return convertFromTimestamp(timestamp)
        }

        // Try to parse as ISO8601 format
        try {
          convertFromString(text)
        } catch (e: DateTimeParseException) {
          throw IllegalArgumentException("Unable to parse time string: $text", e)
        }
      }

      else -> null
    }
  }

  /** Convert from timestamp to target type */
  protected abstract fun convertFromTimestamp(timestamp: Long): T

  /** Convert from string to target type (supports ISO8601, etc.) */
  protected abstract fun convertFromString(text: String): T

  companion object {
    private val ISO_FORMATTERS =
      listOf(
        DateTimeFormatter.ISO_INSTANT,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ISO_ZONED_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ISO_LOCAL_TIME,
      )

    /** Try to parse a string using multiple ISO formats */
    @JvmStatic
    protected fun parseWithMultipleFormats(text: String): Instant? {
      for (formatter in ISO_FORMATTERS) {
        try {
          return when (formatter) {
            DateTimeFormatter.ISO_INSTANT -> Instant.parse(text)
            DateTimeFormatter.ISO_OFFSET_DATE_TIME -> OffsetDateTime.parse(text, formatter).toInstant()
            DateTimeFormatter.ISO_ZONED_DATE_TIME -> ZonedDateTime.parse(text, formatter).toInstant()
            DateTimeFormatter.ISO_LOCAL_DATE_TIME -> LocalDateTime.parse(text, formatter).toInstant(ZoneOffset.UTC)
            DateTimeFormatter.ISO_LOCAL_DATE -> LocalDate.parse(text, formatter).atStartOfDay(ZoneOffset.UTC).toInstant()
            DateTimeFormatter.ISO_LOCAL_TIME -> {
              val time = LocalTime.parse(text, formatter)
              LocalDate.now().atTime(time).toInstant(ZoneOffset.UTC)
            }

            else -> continue
          }
        } catch (e: DateTimeParseException) {
          continue
        }
      }
      return null
    }
  }
}
