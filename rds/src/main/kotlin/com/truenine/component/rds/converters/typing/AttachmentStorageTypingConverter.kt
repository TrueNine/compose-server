package com.truenine.component.rds.converters.typing

import com.truenine.component.rds.typing.AttachmentStorageTyping
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Component
@Converter
class AttachmentStorageTypingConverter : AttributeConverter<AttachmentStorageTyping?, String?> {
  override fun convertToDatabaseColumn(attribute: AttachmentStorageTyping?): String? = attribute?.getValue()
  override fun convertToEntityAttribute(dbData: String?): AttachmentStorageTyping? = AttachmentStorageTyping.findVal(dbData)
}

