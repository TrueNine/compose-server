package net.yan100.compose.rds.converters.typing

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.typing.OrderStatusTyping
import org.springframework.stereotype.Component

@Component
@Converter
class OrderStatusTypingConverter : AttributeConverter<OrderStatusTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: OrderStatusTyping?): Int? {
    return attribute?.getValue()
  }

  override fun convertToEntityAttribute(dbData: Int?): OrderStatusTyping? {
    return OrderStatusTyping.findVal(dbData)
  }
}