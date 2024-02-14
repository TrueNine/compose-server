package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.rds.typing.RuleTyping
import org.springframework.stereotype.Component

@Component
@Converter
class RuleTypingConverter : AttributeConverter<RuleTyping?, Int?> {
    override fun convertToDatabaseColumn(attribute: RuleTyping?) = attribute?.value
    override fun convertToEntityAttribute(dbData: Int?): RuleTyping? = RuleTyping.findVal(dbData)
}
