package net.yan100.compose.core.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import net.yan100.compose.core.lang.AnyTyping

class AnyTypingDeserializer :JsonDeserializer<AnyTyping?>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): AnyTyping? {
    TODO("Not yet implemented")
  }
}
