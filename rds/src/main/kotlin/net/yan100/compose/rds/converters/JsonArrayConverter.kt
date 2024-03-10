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
class JsonArrayConverter(private val mapper: ObjectMapper) :
  AttributeConverter<MutableList<String>, String> {
  override fun convertToDatabaseColumn(attribute: MutableList<String>?): String? =
    attribute?.run { mapper.writeValueAsString(attribute) }

  override fun convertToEntityAttribute(dbData: String?): MutableList<String>? =
    dbData?.run { mapper.readValue(dbData) }
}
