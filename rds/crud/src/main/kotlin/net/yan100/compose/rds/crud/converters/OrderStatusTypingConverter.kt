package net.yan100.compose.rds.crud.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.core.typing.OrderStatusTyping
import org.springframework.stereotype.Component

@Component
@Converter
class OrderStatusTypingConverter :
  AttributeConverter<OrderStatusTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: OrderStatusTyping?): Int? {
    return attribute?.value
  }

  override fun convertToEntityAttribute(dbData: Int?): OrderStatusTyping? {
    return OrderStatusTyping.findVal(dbData)
  }
}
