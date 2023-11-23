package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.DocumentTyping
import org.springframework.stereotype.Component

@Component
@Converter
class DocumentTypingConverter : AttributeConverter<DocumentTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: DocumentTyping?): Int? = attribute?.getValue()
  override fun convertToEntityAttribute(dbData: Int?): DocumentTyping? = DocumentTyping.findVal(dbData)
}
