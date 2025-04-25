package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import net.yan100.compose.toDate
import net.yan100.compose.toLong
import java.time.LocalDate
import java.time.ZoneOffset

class LocalDateSerializerX(private val zoneOffset: ZoneOffset) :
  AbstractTypedSerializer<LocalDate>() {
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
