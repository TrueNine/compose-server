package com.truenine.component.rds.converters

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.models.TableRowChangeSerializableObjectModel
import jakarta.annotation.Resource
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Component
@Converter
class TableRowChangeRecordConverter :
  AttributeConverter<TableRowChangeSerializableObjectModel, String> {
  init {
    log.debug("注册 备份删除表converter = {}", this)
  }

  @Resource
  private lateinit var mapper: ObjectMapper;

  override fun convertToDatabaseColumn(attribute: TableRowChangeSerializableObjectModel?): String? {
    log.debug("对对象 {} 进行转换", attribute)
    return if (null != attribute)
      mapper.writeValueAsString(attribute)
    else null
  }

  override fun convertToEntityAttribute(dbData: String?): TableRowChangeSerializableObjectModel? {
    return if (null != dbData)
      mapper.readValue(
        dbData,
        TableRowChangeSerializableObjectModel::class.java
      )
    else null
  }

  companion object {
    private val log = LogKt.getLog(TableRowChangeRecordConverter::class)
  }
}
