package io.github.truenine.composeserver.depend.jackson.serializers

import io.github.truenine.composeserver.IAnyEnum
import io.github.truenine.composeserver.IIntEnum
import io.github.truenine.composeserver.IStringEnum
import kotlin.reflect.KClass
import tools.jackson.core.JsonParser
import tools.jackson.core.JsonToken
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.deser.std.StdDeserializer

@Deprecated(message = "API surface is too heavy", level = DeprecationLevel.ERROR)
class AnyEnumConverter(typingType: KClass<Enum<*>>) : StdDeserializer<Enum<*>>(Enum::class.java) {
  private var isIntEnum: Boolean = IIntEnum::class.java.isAssignableFrom(typingType.java)
  private var isStringEnum: Boolean = IStringEnum::class.java.isAssignableFrom(typingType.java)
  private val enumValueMap: Map<Any, Enum<*>> = typingType.java.enumConstants.associateBy { (it as IAnyEnum).value }
  private val enumNameMap: Map<String, Enum<*>> = typingType.java.enumConstants.associateBy { it.name }
  private val enumOrdinalMap: Map<Int, Enum<*>> = typingType.java.enumConstants.associateBy { it.ordinal }

  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Enum<*>? {
    val token = p?.currentToken()
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
