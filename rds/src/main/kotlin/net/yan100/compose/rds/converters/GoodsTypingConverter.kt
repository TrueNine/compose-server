package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.GoodsTyping
import org.springframework.stereotype.Component

@Component
@Converter
class GoodsTypingConverter : AttributeConverter<GoodsTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: GoodsTyping?): Int? = attribute?.value
  override fun convertToEntityAttribute(dbData: Int?): GoodsTyping? = GoodsTyping.findVal(dbData)
}
