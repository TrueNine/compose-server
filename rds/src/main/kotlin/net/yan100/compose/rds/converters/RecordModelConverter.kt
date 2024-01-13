package net.yan100.compose.rds.converters

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.core.models.DataRecord
import org.springframework.stereotype.Component

@Component
@Converter
class RecordModelConverter : AttributeConverter<DataRecord, String> {
  init {
    log.debug("注册 备份删除表 converter")
  }

  @Resource
  private lateinit var mapper: ObjectMapper

  override fun convertToDatabaseColumn(attribute: DataRecord?): String? {
    log.trace("转换删除对象 = {}", attribute)
    return if (null != attribute)
      mapper.writeValueAsString(attribute)
    else null
  }

  override fun convertToEntityAttribute(dbData: String?): DataRecord? {
    return if (null != dbData)
      mapper.readValue(
        dbData,
        DataRecord::class.java
      )
    else null
  }

  companion object {
    private val log = slf4j(RecordModelConverter::class)
  }
}
