package net.yan100.compose.rds.converters

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

/**
 * 将数据库内的 json 转换为 List<String>
 *
 * @author TrueNine
 * @since 2023-04-19
 */
@Component
@Converter
class JsonArrayConverter(
  private val mapper: ObjectMapper
) : AttributeConverter<MutableList<String>, String> {
  override fun convertToDatabaseColumn(attribute: MutableList<String>?): String? =
    attribute?.run {
      mapper.writeValueAsString(attribute)
    }

  override fun convertToEntityAttribute(dbData: String?): MutableList<String>? =
    dbData?.run {
      mapper.readValue(dbData)
    }
}
