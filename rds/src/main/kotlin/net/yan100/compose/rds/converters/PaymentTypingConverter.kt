package net.yan100.compose.rds.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.typing.PaymentTyping
import org.springframework.stereotype.Component


@Component
@Converter
class PaymentTypingConverter : AttributeConverter<PaymentTyping?, Int?> {
    override fun convertToDatabaseColumn(attribute: PaymentTyping?): Int? = attribute?.value

    override fun convertToEntityAttribute(dbData: Int?): PaymentTyping? = PaymentTyping.findVal(dbData)
}
