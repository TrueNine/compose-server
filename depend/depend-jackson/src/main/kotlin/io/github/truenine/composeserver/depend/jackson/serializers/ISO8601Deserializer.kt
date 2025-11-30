package io.github.truenine.composeserver.depend.jackson.serializers

import io.github.truenine.composeserver.*
import java.time.*
import java.time.temporal.Temporal
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.ValueDeserializer

/**
 * Abstract base class for ISO8601 timestamp deserialization.
 *
 * Handles converting timestamp strings to Java time types (LocalDate, LocalTime, LocalDateTime, etc.).
 *
 * @param T Target time type
 * @param zoneOffset Zone offset used when converting timestamps
 */
abstract class ISO8601Deserializer<T : Temporal>(protected val zoneOffset: ZoneOffset) : ValueDeserializer<T>() {

  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): T? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    return timestamp?.let { convertTimestamp(it) }
  }

  /**
   * Convert a timestamp to the target time type.
   *
   * @param timestamp Timestamp value
   * @return Converted time instance
   */
  protected abstract fun convertTimestamp(timestamp: Long): T

  /** LocalDate deserializer. */
  class LocalDateDeserializerX(zoneOffset: ZoneOffset) : ISO8601Deserializer<LocalDate>(zoneOffset) {
    override fun convertTimestamp(timestamp: Long): LocalDate {
      return timestamp.toLocalDate(zoneOffset)
    }
  }

  /** LocalDateTime deserializer. */
  class LocalDateTimeDeserializerZ(zoneOffset: ZoneOffset) : ISO8601Deserializer<LocalDateTime>(zoneOffset) {
    override fun convertTimestamp(timestamp: Long): LocalDateTime {
      return timestamp.toLocalDateTime(zoneOffset)
    }
  }

  /** LocalTime deserializer. */
  class LocalTimeDeserializerY(zoneOffset: ZoneOffset) : ISO8601Deserializer<LocalTime>(zoneOffset) {
    override fun convertTimestamp(timestamp: Long): LocalTime {
      return timestamp.toLocalTime(zoneOffset)
    }
  }

  companion object {
    /** Backward-compatible alias. */
    @JvmField val LocalDateDeserializer = LocalDateDeserializerX::class.java

    /** Backward-compatible alias. */
    @JvmField val LocalDateTimeDeserializer = LocalDateTimeDeserializerZ::class.java

    /** Backward-compatible alias. */
    @JvmField val LocalTimeDeserializer = LocalTimeDeserializerY::class.java

    /**
     * Create a LocalDate deserializer instance.
     *
     * @param zoneOffset Zone offset
     * @return LocalDate deserializer
     */
    @JvmStatic
    fun forLocalDate(zoneOffset: ZoneOffset): LocalDateDeserializerX {
      return LocalDateDeserializerX(zoneOffset)
    }

    /**
     * Create a LocalDateTime deserializer instance.
     *
     * @param zoneOffset Zone offset
     * @return LocalDateTime deserializer
     */
    @JvmStatic
    fun forLocalDateTime(zoneOffset: ZoneOffset): LocalDateTimeDeserializerZ {
      return LocalDateTimeDeserializerZ(zoneOffset)
    }

    /**
     * Create a LocalTime deserializer instance.
     *
     * @param zoneOffset Zone offset
     * @return LocalTime deserializer
     */
    @JvmStatic
    fun forLocalTime(zoneOffset: ZoneOffset): LocalTimeDeserializerY {
      return LocalTimeDeserializerY(zoneOffset)
    }

    /**
     * Alias for LocalDateDeserializer, kept for backward compatibility.
     *
     * @param zoneOffset Zone offset
     * @return LocalDate deserializer
     */
    @JvmStatic
    fun LocalDateDeserializer(zoneOffset: ZoneOffset): LocalDateDeserializerX {
      return LocalDateDeserializerX(zoneOffset)
    }

    /**
     * Alias for LocalDateTimeDeserializer, kept for backward compatibility.
     *
     * @param zoneOffset Zone offset
     * @return LocalDateTime deserializer
     */
    @JvmStatic
    fun LocalDateTimeDeserializer(zoneOffset: ZoneOffset): LocalDateTimeDeserializerZ {
      return LocalDateTimeDeserializerZ(zoneOffset)
    }

    /**
     * Alias for LocalTimeDeserializer, kept for backward compatibility.
     *
     * @param zoneOffset Zone offset
     * @return LocalTime deserializer
     */
    @JvmStatic
    fun LocalTimeDeserializer(zoneOffset: ZoneOffset): LocalTimeDeserializerY {
      return LocalTimeDeserializerY(zoneOffset)
    }
  }
}
