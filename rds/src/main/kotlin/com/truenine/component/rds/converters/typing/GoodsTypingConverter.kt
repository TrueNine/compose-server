package com.truenine.component.rds.typing

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Component
@Converter
class GoodsTypingConverter : AttributeConverter<GoodsTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: GoodsTyping?): Int? = attribute?.getValue()

  override fun convertToEntityAttribute(dbData: Int?): GoodsTyping? = GoodsTyping.findVal(dbData)
}
