package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.AuditTyping
import org.springframework.stereotype.Component

/**
 * # 审核状态转换器
 */
@Component
@Converter
class AuditTypingConverter : AttributeConverter<AuditTyping?, Int?> {
  override fun convertToDatabaseColumn(attribute: AuditTyping?): Int? = attribute?.v
  override fun convertToEntityAttribute(dbData: Int?): AuditTyping? = AuditTyping.findVal(dbData)
}
