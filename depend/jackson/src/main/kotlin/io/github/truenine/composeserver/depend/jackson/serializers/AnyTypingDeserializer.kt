package io.github.truenine.composeserver.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import io.github.truenine.composeserver.typing.AnyTyping
import io.github.truenine.composeserver.typing.IntTyping
import io.github.truenine.composeserver.typing.StringTyping
import kotlin.reflect.KClass

@Deprecated(message = "API 负担过大", level = DeprecationLevel.ERROR)
class AnyTypingDeserializer(typingType: KClass<Enum<*>>) : StdDeserializer<Enum<*>>(Enum::class.java) {
  private var isIntEnum: Boolean = IntTyping::class.java.isAssignableFrom(typingType.java)
  private var isStringEnum: Boolean = StringTyping::class.java.isAssignableFrom(typingType.java)
  private val enumValueMap: Map<Any, Enum<*>> = typingType.java.enumConstants.associateBy { (it as AnyTyping).value }
  private val enumNameMap: Map<String, Enum<*>> = typingType.java.enumConstants.associateBy { it.name }
  private val enumOrdinalMap: Map<Int, Enum<*>> = typingType.java.enumConstants.associateBy { it.ordinal }

  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Enum<*>? {
    val token = p?.currentToken
    return when (token) {
      JsonToken.VALUE_STRING -> {
        val nameOrValue = p.text
        if (isStringEnum) enumValueMap[nameOrValue] else enumNameMap[nameOrValue]
      }

      JsonToken.VALUE_NUMBER_INT -> {
        val intValue = p.intValue
        if (isIntEnum) enumValueMap[intValue] else enumOrdinalMap[intValue]
      }

      else -> null
    }
  }
}
