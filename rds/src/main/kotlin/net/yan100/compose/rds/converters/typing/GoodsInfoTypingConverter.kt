package net.yan100.compose.rds.converters.typing

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.GoodsInfoTyping
import org.springframework.stereotype.Component

@Converter
@Component
class GoodsInfoTypingConverter : AttributeConverter<GoodsInfoTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: GoodsInfoTyping?): Int? = attribute?.getValue()
  override fun convertToEntityAttribute(dbData: Int?): GoodsInfoTyping? = GoodsInfoTyping.findVal(dbData)
}
