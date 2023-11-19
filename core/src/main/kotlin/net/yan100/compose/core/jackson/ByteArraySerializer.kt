package net.yan100.compose.core.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class ByteArraySerializer : JsonSerializer<ByteArray>() {
  override fun serialize(value: ByteArray?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    gen?.writeArray(value?.map { it.toInt() }?.toIntArray(), 0, value?.size ?: 0)
  }
}
