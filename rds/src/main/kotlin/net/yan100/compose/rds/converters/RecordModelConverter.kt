package net.yan100.compose.rds.converters

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.lang.slf4j
import org.springframework.stereotype.Component

@Component
@Converter
class RecordModelConverter :
  AttributeConverter<net.yan100.compose.rds.base.RecordModel, String> {
  init {
    log.debug("注册 备份删除表converter = {}", this)
  }

  @Resource
  private lateinit var mapper: ObjectMapper

  override fun convertToDatabaseColumn(attribute: net.yan100.compose.rds.base.RecordModel?): String? {
    log.trace("转换删除对象 = {}", attribute)
    return if (null != attribute)
      mapper.writeValueAsString(attribute)
    else null
  }

  override fun convertToEntityAttribute(dbData: String?): net.yan100.compose.rds.base.RecordModel? {
    return if (null != dbData)
      mapper.readValue(
        dbData,
        net.yan100.compose.rds.base.RecordModel::class.java
      )
    else null
  }

  companion object {
    private val log = slf4j(RecordModelConverter::class)
  }
}
