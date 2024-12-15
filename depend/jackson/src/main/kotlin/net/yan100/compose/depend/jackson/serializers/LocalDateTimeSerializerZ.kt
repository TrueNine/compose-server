package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.SerializerProvider
import net.yan100.compose.core.toDate
import net.yan100.compose.core.toLong
import java.time.LocalDateTime
import java.time.ZoneOffset

class LocalDateTimeSerializerZ(private val zoneOffset: ZoneOffset) : AbstractTypedSerializer<LocalDateTime>(JsonToken.VALUE_NUMBER_INT) {
  override fun handledType(): Class<LocalDateTime> {
    return LocalDateTime::class.java
  }

  override fun serialize(
    value: LocalDateTime,
    gen: JsonGenerator?,
    serializers: SerializerProvider?
  ) {
    val v = value.toDate(zoneOffset).toLong()
    gen?.writeNumber(v)
  }
}
