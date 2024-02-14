package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.AttachmentTyping
import org.springframework.stereotype.Component

/**
 * 附件类型转换器
 * @author TrueNine
 * @since 2023-06-08
 */
@Component
@Converter
class AttachmentTypingConverter : AttributeConverter<AttachmentTyping?, Int?> {
    override fun convertToDatabaseColumn(attribute: AttachmentTyping?): Int? = attribute?.value
    override fun convertToEntityAttribute(dbData: Int?): AttachmentTyping? = AttachmentTyping.findVal(dbData)
}

