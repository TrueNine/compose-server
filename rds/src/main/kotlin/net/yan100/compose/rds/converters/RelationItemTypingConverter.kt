package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.RelationItemTyping
import org.springframework.stereotype.Component

@Component
@Converter
class RelationItemTypingConverter : AttributeConverter<RelationItemTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: RelationItemTyping?): Int? = attribute?.value
  override fun convertToEntityAttribute(dbData: Int?): RelationItemTyping? = RelationItemTyping.findVal(dbData)
}
