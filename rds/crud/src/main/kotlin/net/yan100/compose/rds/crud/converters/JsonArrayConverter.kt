package net.yan100.compose.rds.crud.converters

import com.fasterxml.jackson.databind.ObjectMapper
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
class JsonArrayConverter(private val mapper: ObjectMapper) :
  AttributeConverter<MutableList<String>, String> {
  override fun convertToDatabaseColumn(
    attribute: MutableList<String>?
  ): String? = attribute?.run { mapper.writeValueAsString(attribute) }

  @Suppress("UNCHECK_CAST")
  override fun convertToEntityAttribute(dbData: String?): MutableList<String>? =
    dbData?.run {
      mapper.readValue(dbData, MutableList::class.java) as? MutableList<String>
    }
}
