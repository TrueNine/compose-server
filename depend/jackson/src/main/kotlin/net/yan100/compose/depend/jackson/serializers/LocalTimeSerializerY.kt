package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import net.yan100.compose.toDate
import net.yan100.compose.toLong
import java.time.LocalTime
import java.time.ZoneOffset

class LocalTimeSerializerY(private val zoneOffset: ZoneOffset) :
  AbstractTypedSerializer<LocalTime>() {
  override fun handledType(): Class<LocalTime> = LocalTime::class.java

  override fun serialize(
    value: LocalTime,
    gen: JsonGenerator?,
    serializers: SerializerProvider?,
  ) {
    gen?.writeNumber(value.toDate(zoneOffset).toLong())
  }
}
