package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.LocalTime
import java.time.ZoneOffset
import net.yan100.compose.core.toDate
import net.yan100.compose.core.toLong

class LocalTimeSerializerY(private val zoneOffset: ZoneOffset) :
  AbstractTypedSerializer<LocalTime>(JsonToken.VALUE_NUMBER_INT) {
  override fun handledType(): Class<LocalTime> = LocalTime::class.java

  override fun serialize(
    value: LocalTime,
    gen: JsonGenerator?,
    serializers: SerializerProvider?,
  ) {
    gen?.writeNumber(value.toDate(zoneOffset).toLong())
  }
}
