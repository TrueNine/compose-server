package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.typing.PayChannelTyping
import org.springframework.stereotype.Component

@Component
@Converter
class PayChannelTypingConverter : AttributeConverter<PayChannelTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: PayChannelTyping?): Int? {
    return attribute?.value
  }

  override fun convertToEntityAttribute(dbData: Int?): PayChannelTyping? {
    return PayChannelTyping.findVal(dbData)
  }
}
