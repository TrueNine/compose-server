package com.truenine.component.rds.converters.typing

import com.truenine.component.rds.typing.GoodsChangeRecordTyping
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Component
@Converter
class GoodsChangeRecordTypingConverter : AttributeConverter<GoodsChangeRecordTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: GoodsChangeRecordTyping?): Int? = attribute?.getValue()
  override fun convertToEntityAttribute(dbData: Int?): GoodsChangeRecordTyping? = GoodsChangeRecordTyping.findVal(dbData)
}
