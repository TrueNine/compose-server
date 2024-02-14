package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.CertContentTyping
import org.springframework.stereotype.Component

@Component
@Converter
class CertContentTypingConverter : AttributeConverter<CertContentTyping?, Int?> {
    override fun convertToDatabaseColumn(attribute: CertContentTyping?): Int? = attribute?.value
    override fun convertToEntityAttribute(dbData: Int?): CertContentTyping? = CertContentTyping.findVal(dbData)
}
