package net.yan100.compose.rds.converters.typing

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.AttachmentTyping
import org.springframework.stereotype.Component

/**
 * 附件类型转换器
 */
@Component
@Converter
class AttachmentStorageTypingConverter : AttributeConverter<AttachmentTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: AttachmentTyping?): Int? = attribute?.getValue()
  override fun convertToEntityAttribute(dbData: Int?): AttachmentTyping? = AttachmentTyping.findVal(dbData)
}

