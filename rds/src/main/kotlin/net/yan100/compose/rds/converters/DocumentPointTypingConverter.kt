package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.DocumentPointTyping
import org.springframework.stereotype.Component

@Component
@Converter
class DocumentPointTypingConverter : AttributeConverter<DocumentPointTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: DocumentPointTyping?): Int? = attribute?.getValue()
  override fun convertToEntityAttribute(dbData: Int?): DocumentPointTyping? = DocumentPointTyping.findVal(dbData)
}
