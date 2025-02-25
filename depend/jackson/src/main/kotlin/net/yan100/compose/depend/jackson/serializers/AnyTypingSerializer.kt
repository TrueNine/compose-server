package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import net.yan100.compose.core.typing.AnyTyping
import net.yan100.compose.core.typing.IntTyping
import net.yan100.compose.core.typing.StringTyping

@Deprecated(message = "API 负担过大", level = DeprecationLevel.ERROR)
class AnyTypingSerializer : JsonSerializer<AnyTyping>() {

  override fun handledType(): Class<AnyTyping> {
    return AnyTyping::class.java
  }

  override fun serialize(
    value: AnyTyping?,
    gen: JsonGenerator?,
    serializers: SerializerProvider?,
  ) {
    when (value) {
      is StringTyping -> gen?.writeString(value.value)
      is IntTyping -> gen?.writeNumber(value.value)
      null -> gen?.writeNull()
      else -> gen?.writeNull()
    }
  }
}
