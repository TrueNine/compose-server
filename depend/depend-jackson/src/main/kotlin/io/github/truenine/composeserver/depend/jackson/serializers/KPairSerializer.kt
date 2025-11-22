package io.github.truenine.composeserver.depend.jackson.serializers

import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer

/**
 * Kotlin Pair serializer.
 *
 * Serializes a Kotlin Pair into JSON array format. Output format: [first, second].
 */
class KPairSerializer : ValueSerializer<Pair<*, *>>() {

  override fun handledType(): Class<Pair<*, *>> = Pair::class.java as Class<Pair<*, *>>

  override fun serialize(value: Pair<*, *>?, gen: JsonGenerator?, ctxt: SerializationContext?) {
    if (gen == null) return

    if (value == null) {
      gen.writeNull()
      return
    }

    gen.writeStartArray()
    ctxt?.writeValue(gen, value.first)
    ctxt?.writeValue(gen, value.second)
    gen.writeEndArray()
  }
}
