package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.DocumentContentTyping
import org.springframework.stereotype.Component

@Component
@Converter
class DocumentContentTypingConverter : AttributeConverter<DocumentContentTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: DocumentContentTyping?): Int? = attribute?.getValue()
  override fun convertToEntityAttribute(dbData: Int?): DocumentContentTyping? = DocumentContentTyping.findVal(dbData)
}
