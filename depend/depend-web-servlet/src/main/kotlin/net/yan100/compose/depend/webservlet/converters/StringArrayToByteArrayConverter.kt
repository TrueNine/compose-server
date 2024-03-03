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

import org.springframework.core.convert.converter.Converter

/**
 * ## 此转换器针对 form data 直接提交的 byte 数组
 *
 * @author TrueNine
 * @since 2024-02-29
 */
class StringArrayToByteArrayConverter : Converter<Array<String>, ByteArray> {
  override fun convert(source: Array<String>): ByteArray {
    return source.map { it.toByte() }.toByteArray()
  }
}
