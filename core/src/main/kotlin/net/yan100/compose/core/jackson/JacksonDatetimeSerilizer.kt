package net.yan100.compose.core.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import net.yan100.compose.core.lang.*
import java.time.*


class LocalDateTimeSerializer(
  private val zoneOffset: ZoneOffset
) : JsonSerializer<LocalDateTime>() {
  override fun serialize(value: LocalDateTime, gen: JsonGenerator?, serializers: SerializerProvider?) {
    gen?.writeNumber(value.toInstant(zoneOffset).toEpochMilli())
  }
}

class LocalDateTimeDeserializer(
  private val zoneOffset: ZoneOffset
) : JsonDeserializer<LocalDateTime>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDateTime? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    return timestamp?.let { t ->
      val instant = Instant.ofEpochMilli(t)
      LocalDateTime.ofInstant(instant, zoneOffset)
    }
  }
}

class LocalDateSerializer(
  private val zoneOffset: ZoneOffset
) : JsonSerializer<LocalDate>() {
  override fun serialize(value: LocalDate, gen: JsonGenerator?, serializers: SerializerProvider?) {
    gen?.writeNumber(value.toDate(zoneOffset).toLong())
  }
}


class LocalDateDeserializer(
  private val zoneOffset: ZoneOffset
) : JsonDeserializer<LocalDate>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDate? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    return timestamp?.let { t ->
      t.toDateOrNull()?.toLocalDate(zoneOffset)
    }
  }
}


class LocalTimeSerializer(
  private val zoneOffset: ZoneOffset
) : JsonSerializer<LocalTime>() {
  override fun serialize(value: LocalTime, gen: JsonGenerator?, serializers: SerializerProvider?) {
    gen?.writeNumber(value.toDate(zoneOffset).toLong())
  }
}

class LocalTimeDeserializer(
  private val zoneOffset: ZoneOffset
) : JsonDeserializer<LocalTime>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalTime? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    return timestamp?.let { t ->
      t.toDateOrNull()?.toLocalTime(zoneOffset)
    }
  }
}
