package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.RelationTyping
import org.springframework.stereotype.Component

@Component
@Converter
class RelationTypingConverter : AttributeConverter<RelationTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: RelationTyping?): Int? = attribute?.value
  override fun convertToEntityAttribute(dbData: Int?): RelationTyping? = RelationTyping.findVal(dbData)
}
