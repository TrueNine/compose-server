package net.yan100.compose.rds.crud.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.core.typing.GoodsTyping
import org.springframework.stereotype.Component

@Component
@Converter
class GoodsTypingConverter : AttributeConverter<GoodsTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: GoodsTyping?): Int? =
    attribute?.value

  override fun convertToEntityAttribute(dbData: Int?): GoodsTyping? =
    GoodsTyping.findVal(dbData)
}
