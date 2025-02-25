package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.LocalDate
import java.time.ZoneOffset
import net.yan100.compose.core.toDate
import net.yan100.compose.core.toLong

class LocalDateSerializerX(private val zoneOffset: ZoneOffset) :
  AbstractTypedSerializer<LocalDate>(JsonToken.VALUE_NUMBER_INT) {
  override fun handledType(): Class<LocalDate> {
    return LocalDate::class.java
  }

  override fun serialize(
    value: LocalDate,
    gen: JsonGenerator?,
    serializers: SerializerProvider?,
  ) {
    gen?.writeNumber(value.toDate(zoneOffset).toLong())
  }
}
