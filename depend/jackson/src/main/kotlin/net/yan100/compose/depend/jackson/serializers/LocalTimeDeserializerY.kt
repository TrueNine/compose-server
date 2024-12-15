package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import net.yan100.compose.core.toLocalTime
import java.time.LocalTime
import java.time.ZoneOffset

class LocalTimeDeserializerY(private val zoneOffset: ZoneOffset) : LocalTimeDeserializer() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalTime? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    val v = timestamp?.toLocalTime(zoneOffset)
    return v
  }
}
