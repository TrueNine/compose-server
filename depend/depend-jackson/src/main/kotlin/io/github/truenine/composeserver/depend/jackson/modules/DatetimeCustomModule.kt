package io.github.truenine.composeserver.depend.jackson.modules

import io.github.truenine.composeserver.depend.jackson.serializers.InstantTimestampDeserializer
import io.github.truenine.composeserver.depend.jackson.serializers.InstantTimestampSerializer
import io.github.truenine.composeserver.depend.jackson.serializers.LocalDateTimeTimestampDeserializer
import io.github.truenine.composeserver.depend.jackson.serializers.LocalDateTimeTimestampSerializer
import io.github.truenine.composeserver.depend.jackson.serializers.LocalDateTimestampDeserializer
import io.github.truenine.composeserver.depend.jackson.serializers.LocalDateTimestampSerializer
import io.github.truenine.composeserver.depend.jackson.serializers.LocalTimeTimestampDeserializer
import io.github.truenine.composeserver.depend.jackson.serializers.LocalTimeTimestampSerializer
import io.github.truenine.composeserver.depend.jackson.serializers.OffsetDateTimeTimestampDeserializer
import io.github.truenine.composeserver.depend.jackson.serializers.OffsetDateTimeTimestampSerializer
import io.github.truenine.composeserver.depend.jackson.serializers.ZonedDateTimeTimestampDeserializer
import io.github.truenine.composeserver.depend.jackson.serializers.ZonedDateTimeTimestampSerializer
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.module.SimpleDeserializers
import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.module.SimpleSerializers

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
