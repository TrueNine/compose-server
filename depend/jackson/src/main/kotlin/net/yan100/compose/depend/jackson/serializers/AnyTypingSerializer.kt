package net.yan100.compose.depend.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import net.yan100.compose.core.typing.AnyTyping
import net.yan100.compose.core.typing.IntTyping
import net.yan100.compose.core.typing.StringTyping

class AnyTypingSerializer : JsonSerializer<AnyTyping>() {
  @Suppress("UNCHECKED_CAST")
  override fun handledType(): Class<AnyTyping>? {
    return AnyTyping::class.java as? Class<AnyTyping>?
  }

  override fun serialize(
    value: AnyTyping?,
    gen: JsonGenerator?,
    serializers: SerializerProvider?
  ) {
    when (value) {
      is StringTyping -> gen?.writeString(value.value)
      is IntTyping -> gen?.writeNumber(value.value)
      null -> gen?.writeNull()
      else -> gen?.writeNull()
    }
  }
}
