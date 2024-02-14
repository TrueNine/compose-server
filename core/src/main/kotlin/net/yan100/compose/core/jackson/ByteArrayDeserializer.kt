package net.yan100.compose.core.jackson

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

// FIXME 这种序列化效率太低
class ByteArrayDeserializer : JsonDeserializer<ByteArray?>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): ByteArray? {
        return p?.let {
            val str = it.readValueAsTree<JsonNode>()
            val byteArray = ByteArray(str.size())
            for (i in 0 until str.size()) {
                val j = str.get(i)
                if (j.isNumber) byteArray[i] = j.asInt().toByte()
                else throw JsonParseException("序列化为 byte 错误")
            }
            byteArray
        }
    }
}



