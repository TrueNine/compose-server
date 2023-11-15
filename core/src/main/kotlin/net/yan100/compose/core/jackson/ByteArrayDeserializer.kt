package net.yan100.compose.core.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class ByteArrayDeserializer : JsonDeserializer<ByteArray>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): ByteArray {
    return p!!.binaryValue
  }
}


