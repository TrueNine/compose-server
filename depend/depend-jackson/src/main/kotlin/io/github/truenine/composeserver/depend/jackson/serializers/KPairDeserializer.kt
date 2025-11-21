package io.github.truenine.composeserver.depend.jackson.serializers

import tools.jackson.core.JsonParser
import tools.jackson.core.JsonToken
import tools.jackson.databind.DatabindException
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.ValueDeserializer

/**
 * Kotlin Pair deserializer.
 *
 * Supports deserializing JSON array format into a Kotlin Pair instance. Expected JSON format: [first, second].
 */
class KPairDeserializer : ValueDeserializer<Pair<*, *>>() {

  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Pair<*, *> {
    if (p == null) {
      throw DatabindException.from(ctxt, "JsonParser cannot be null for Pair deserialization")
    }

    // Check that we are at the start of an array
    if (p.currentToken() != JsonToken.START_ARRAY) {
      throw DatabindException.from(ctxt, "Expected START_ARRAY token for Pair deserialization, got: ${p.currentToken()}")
    }

    // Read first element
    p.nextToken()
    val first = p.readValueAs(Any::class.java)

    // Read second element
    p.nextToken()
    val second = p.readValueAs(Any::class.java)

    // Ensure array ends after two elements
    p.nextToken()
    if (p.currentToken() != JsonToken.END_ARRAY) {
      throw DatabindException.from(ctxt, "Expected exactly 2 elements in array for Pair deserialization")
    }

    return Pair(first, second)
  }
}
