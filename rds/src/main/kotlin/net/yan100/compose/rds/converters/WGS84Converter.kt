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

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.domain.Coordinate
import org.springframework.stereotype.Component


/**
 * 将数据库内以字符串存储的坐标转换为 x y 形式
 *
 * @author TrueNine
 * @since 2023-04-19
 */
@Component
@Converter(autoApply = true)
class WGS84Converter : AttributeConverter<Coordinate, String> {
  override fun convertToDatabaseColumn(attribute: Coordinate?): String? = attribute?.run { "P(${attribute.x},${attribute.y})" }

  override fun convertToEntityAttribute(dbData: String?): Coordinate? {
    return dbData?.let { exp ->
      val group = exp.replace(Regex("""(?i)P\(|\)"""), "").split(",").map { it.trim().toBigDecimalOrNull() }
      Coordinate(group[0], group[1])
    }
  }
}
