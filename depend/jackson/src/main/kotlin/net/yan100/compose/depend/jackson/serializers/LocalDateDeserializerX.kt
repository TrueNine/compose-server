package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import net.yan100.compose.toLocalDate
import java.time.LocalDate
import java.time.ZoneOffset

class LocalDateDeserializerX(private val zoneOffset: ZoneOffset) :
  JsonDeserializer<LocalDate>() {
  override fun deserialize(
    p: JsonParser?,
    ctxt: DeserializationContext?,
  ): LocalDate? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    val v = timestamp?.toLocalDate(zoneOffset)
    return v
  }
}
