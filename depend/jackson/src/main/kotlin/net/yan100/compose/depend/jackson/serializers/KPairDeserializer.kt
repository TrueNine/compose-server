package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class KPairDeserializer : JsonDeserializer<Pair<*, *>>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Pair<*, *> {
    val values: Array<Any> = p!!.readValueAs(Array<Any>::class.java)
    return Pair(values[0], values[1])
  }
}
