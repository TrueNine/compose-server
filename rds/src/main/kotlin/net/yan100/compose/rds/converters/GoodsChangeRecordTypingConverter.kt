package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.GoodsChangeRecordTyping
import org.springframework.stereotype.Component

@Component
@Converter
class GoodsChangeRecordTypingConverter : AttributeConverter<GoodsChangeRecordTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: GoodsChangeRecordTyping?): Int? = attribute?.getValue()
  override fun convertToEntityAttribute(dbData: Int?): GoodsChangeRecordTyping? = GoodsChangeRecordTyping.findVal(dbData)
}
