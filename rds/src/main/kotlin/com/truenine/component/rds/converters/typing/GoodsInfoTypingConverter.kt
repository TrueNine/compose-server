package com.truenine.component.rds.converters.typing

import com.truenine.component.rds.typing.GoodsInfoTyping
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter
@Component
class GoodsInfoTypingConverter : AttributeConverter<GoodsInfoTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: GoodsInfoTyping?): Int? = attribute?.getValue()
  override fun convertToEntityAttribute(dbData: Int?): GoodsInfoTyping? = GoodsInfoTyping.findVal(dbData)
}
