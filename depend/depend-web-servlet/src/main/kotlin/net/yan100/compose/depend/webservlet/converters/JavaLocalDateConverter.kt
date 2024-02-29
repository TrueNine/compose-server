/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.depend.webservlet.converters

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import kotlin.reflect.KClass
import net.yan100.compose.core.extensionfunctions.hasTextRun
import net.yan100.compose.core.extensionfunctions.mutableLockMapOf
import net.yan100.compose.core.extensionfunctions.toLocalDate
import net.yan100.compose.core.log.slf4j
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory

private val log = slf4j(JavaLocalDateConverter::class)

open class JavaLocalDateConverter : Converter<String?, LocalDate?> {
  override fun convert(source: String): LocalDate? {
    log.trace("转换日期 = {}", source)
    return source.toLongOrNull()?.toLocalDate()
  }
}
