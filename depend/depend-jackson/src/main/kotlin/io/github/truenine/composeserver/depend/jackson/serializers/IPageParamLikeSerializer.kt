package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import io.github.truenine.composeserver.Pq
import io.github.truenine.composeserver.domain.IPageParam

/**
 * Deserializer for IPageParam and IPageParamLike interfaces.
 *
 * Supports deserialization from a JSON object to an IPageParam instance.
 * Expected JSON format: {"o": offset, "s": pageSize, "u": unPage}
 */
class IPageParamLikeSerializer : JsonDeserializer<IPageParam>() {

  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): IPageParam? {
    if (p == null) {
      throw JsonMappingException.from(ctxt, "JsonParser cannot be null for IPageParam deserialization")
    }

    // Check if the current token is the start of an object
    if (p.currentToken != JsonToken.START_OBJECT) {
      throw JsonMappingException.from(ctxt, "Expected START_OBJECT token for IPageParam deserialization, got: ${p.currentToken}")
    }

    var offset: Int? = null
    var pageSize: Int? = null
    var unPage: Boolean? = null

    // Iterate over the fields of the JSON object
    while (p.nextToken() != JsonToken.END_OBJECT) {
      val fieldName = p.currentName()
      p.nextToken() // Move to the field value

      when (fieldName) {
        "o" -> {
          offset = p.intValueOrNull()
        }

        "s" -> {
          pageSize = p.intValueOrNull()
        }

        "u" -> {
          unPage = p.booleanValueOrNull()
        }

        else -> {
          // Skip unknown fields
          p.skipChildren()
        }
      }
    }

    // Use the Pq factory method to create an IPageParam instance
    return Pq[offset, pageSize, unPage]
  }

  /** Extension function to safely parse an Int value */
  private fun JsonParser.intValueOrNull(): Int? {
    return when {
      currentToken.isNumeric -> intValue
      currentToken == JsonToken.VALUE_NULL -> null
      else -> throw JsonMappingException.from(null as DeserializationContext?, "Expected numeric value for int field, got: $currentToken")
    }
  }

  /** Extension function to safely parse a Boolean value */
  private fun JsonParser.booleanValueOrNull(): Boolean? {
    return when (currentToken) {
      JsonToken.VALUE_TRUE -> true
      JsonToken.VALUE_FALSE -> false
      JsonToken.VALUE_NULL -> null
      else -> throw JsonMappingException.from(null as DeserializationContext?, "Expected boolean value for boolean field, got: $currentToken")
    }
  }
}
