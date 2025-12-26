package io.github.truenine.composeserver.depend.jackson.modules

import io.github.truenine.composeserver.depend.jackson.serializers.*
import java.time.*
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.module.*

/**
 * Custom module for date-time types.
 *
 * Uses timestamp serializers to serialize all date-time types to UTC timestamps (in milliseconds), ensuring time-zone independence.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class DatetimeCustomModule : SimpleModule() {

  override fun setupModule(context: SetupContext?) {
    super.setupModule(context)

    // Create a container for timestamp serializers
    val serializers = ArrayList<ValueSerializer<*>>(6)

    // Register timestamp serializers for all date-time types, ensuring highest priority
    serializers.add(LocalDateTimeTimestampSerializer())
    serializers.add(LocalDateTimestampSerializer())
    serializers.add(LocalTimeTimestampSerializer())
    serializers.add(InstantTimestampSerializer())
    serializers.add(ZonedDateTimeTimestampSerializer())
    serializers.add(OffsetDateTimeTimestampSerializer())

    // Add to the context
    context?.addSerializers(SimpleSerializers(serializers))

    // Create a container for timestamp deserializers
    val deserializers = ArrayList<ValueDeserializer<*>>(6)

    deserializers.add(LocalDateTimeTimestampDeserializer())
    deserializers.add(LocalDateTimestampDeserializer())
    deserializers.add(LocalTimeTimestampDeserializer())
    deserializers.add(InstantTimestampDeserializer())
    deserializers.add(ZonedDateTimeTimestampDeserializer())
    deserializers.add(OffsetDateTimeTimestampDeserializer())

    context?.addDeserializers(
      SimpleDeserializers(
        buildMap {
          put(LocalDateTime::class.java, LocalDateTimeTimestampDeserializer())
          put(LocalDate::class.java, LocalDateTimestampDeserializer())
          put(LocalTime::class.java, LocalTimeTimestampDeserializer())
          put(Instant::class.java, InstantTimestampDeserializer())
          put(ZonedDateTime::class.java, ZonedDateTimeTimestampDeserializer())
          put(OffsetDateTime::class.java, OffsetDateTimeTimestampDeserializer())
        }
      )
    )
  }
}
