package io.github.truenine.composeserver.depend.jackson.serializers

import io.github.truenine.composeserver.toMillis
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonToken
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.jsontype.TypeSerializer
import java.time.*

/**
 * # ISO8601 millisecond timestamp serializer
 *
 * Provides JSON serialization for Java time types using ISO8601-style timestamps.
 *
 * @param T Time type to serialize, supports LocalDate, LocalDateTime and LocalTime
 * @author TrueNine
 * @since 2025-04-26
 */
sealed class ISO8601Serializer<T> : ValueSerializer<T>() {
  /**
   * ## Serialization with type information.
   *
   * @param value Value to serialize
   * @param gen JSON generator
   * @param serializers Serialization provider
   * @param typeSer Type serializer
   */
  override fun serializeWithType(value: T, gen: JsonGenerator?, ctxt: SerializationContext?, typeSer: TypeSerializer) {
    // Use VALUE_NUMBER_INT as the shape because date/time types are serialized as numbers
    val shape = JsonToken.VALUE_NUMBER_INT
    val typeIdDef = typeSer.typeId(value, shape)
    typeSer.writeTypePrefix(gen, ctxt, typeIdDef)
    serialize(value, gen, ctxt)
    typeSer.writeTypeSuffix(gen, ctxt, typeIdDef)
  }

  /**
   * # LocalDate ISO8601 serializer
   *
   * Converts LocalDate to an ISO8601-style timestamp.
   *
   * @param zoneOffset Zone offset used for conversion
   */
  class ISO8601DateSerializer(private val zoneOffset: ZoneOffset = ZoneOffset.UTC) : ISO8601Serializer<LocalDate>() {
    /** No-arg constructor for Jackson. */
    constructor() : this(ZoneOffset.UTC)

    /**
     * ## Handled type.
     *
     * @return LocalDate class
     */
    override fun handledType(): Class<LocalDate> = LocalDate::class.java

    /**
     * ## Serialize LocalDate.
     *
     * @param value LocalDate value
     * @param gen JSON generator
     * @param serializers Serialization provider
     */
    override fun serialize(value: LocalDate, gen: JsonGenerator?, ctxt: SerializationContext?) {
      gen?.writeNumber(value.toMillis(zoneOffset))
    }
  }

  /**
   * # LocalDateTime ISO8601 serializer
   *
   * Converts LocalDateTime to an ISO8601-style timestamp.
   *
   * @param zoneOffset Zone offset used for conversion
   */
  class ISO8601DateTimeSerializer(private val zoneOffset: ZoneOffset = ZoneOffset.UTC) : ISO8601Serializer<LocalDateTime>() {
    /** No-arg constructor for Jackson. */
    constructor() : this(ZoneOffset.UTC)

    /**
     * ## Handled type.
     *
     * @return LocalDateTime class
     */
    override fun handledType(): Class<LocalDateTime> = LocalDateTime::class.java

    /**
     * ## Serialize LocalDateTime.
     *
     * @param value LocalDateTime value
     * @param gen JSON generator
     * @param serializers Serialization provider
     */
    override fun serialize(value: LocalDateTime, gen: JsonGenerator?, ctxt: SerializationContext?) {
      gen?.writeNumber(value.toMillis(zoneOffset))
    }
  }

  /**
   * # LocalTime ISO8601 serializer
   *
   * Converts LocalTime to an ISO8601-style timestamp.
   *
   * @param zoneOffset Zone offset used for conversion
   */
  class ISO8601TimeSerializer(private val zoneOffset: ZoneOffset = ZoneOffset.UTC) : ISO8601Serializer<LocalTime>() {
    /** No-arg constructor for Jackson. */
    constructor() : this(ZoneOffset.UTC)

    /**
     * ## Handled type.
     *
     * @return LocalTime class
     */
    override fun handledType(): Class<LocalTime> = LocalTime::class.java

    /**
     * ## Serialize LocalTime.
     *
     * @param value LocalTime value
     * @param gen JSON generator
     * @param serializers Serialization provider
     */
    override fun serialize(value: LocalTime, gen: JsonGenerator?, ctxt: SerializationContext?) {
      gen?.writeNumber(value.toMillis(zoneOffset))
    }
  }
}
