/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.converters

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.log.slf4j
import net.yan100.compose.rds.core.models.DataRecord
import org.springframework.stereotype.Component

@Component
@Converter
class RecordModelConverter : AttributeConverter<DataRecord, String> {
  init {
    log.debug("注册 备份删除表 converter")
  }

  @Resource private lateinit var mapper: ObjectMapper

  override fun convertToDatabaseColumn(attribute: DataRecord?): String? {
    log.trace("转换删除对象 = {}", attribute)
    return if (null != attribute) mapper.writeValueAsString(attribute) else null
  }

  override fun convertToEntityAttribute(dbData: String?): DataRecord? {
    return if (null != dbData) mapper.readValue(dbData, DataRecord::class.java) else null
  }

  companion object {
    private val log = slf4j(RecordModelConverter::class)
  }
}
