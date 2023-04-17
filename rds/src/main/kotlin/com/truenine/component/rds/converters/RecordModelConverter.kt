package com.truenine.component.rds.converters

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.base.RecordModel
import jakarta.annotation.Resource
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Component
@Converter
class RecordModelConverter :
  AttributeConverter<RecordModel, String> {
  init {
    log.debug("注册 备份删除表converter = {}", this)
  }

  @Resource
  private lateinit var mapper: ObjectMapper;

  override fun convertToDatabaseColumn(attribute: RecordModel?): String? {
    log.trace("转换删除对象 = {}", attribute)
    return if (null != attribute)
      mapper.writeValueAsString(attribute)
    else null
  }

  override fun convertToEntityAttribute(dbData: String?): RecordModel? {
    return if (null != dbData)
      mapper.readValue(
        dbData,
        RecordModel::class.java
      )
    else null
  }

  companion object {
    private val log = LogKt.getLog(RecordModelConverter::class)
  }
}
