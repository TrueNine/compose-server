package net.yan100.compose.rds.converters.typing

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.AttachmentStorageTyping
import org.springframework.stereotype.Component

@Component
@Converter
class AttachmentStorageTypingConverter : AttributeConverter<AttachmentStorageTyping?, String?> {
  override fun convertToDatabaseColumn(attribute: AttachmentStorageTyping?): String? = attribute?.getValue()
  override fun convertToEntityAttribute(dbData: String?): AttachmentStorageTyping? = AttachmentStorageTyping.findVal(dbData)
}

