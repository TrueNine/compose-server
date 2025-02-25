package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import java.time.LocalDateTime
import java.time.ZoneOffset
import net.yan100.compose.core.toLocalDateTime

class LocalDateTimeDeserializerZ(private val zoneOffset: ZoneOffset) :
  LocalDateTimeDeserializer() {
  override fun deserialize(
    p: JsonParser?,
    ctxt: DeserializationContext?,
  ): LocalDateTime? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    val v = timestamp?.toLocalDateTime(zoneOffset)
    return v
  }
}
