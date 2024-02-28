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
package net.yan100.compose.core.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import net.yan100.compose.core.annotations.BigIntegerAsString

/**
 * jackson json 长整型转字符串序列化器
 *
 * @param <Long>
 * @since 2023-05-09
 */
class LongAsStringSerializer : JsonSerializer<Long?>(), ContextualSerializer {

  override fun serialize(value: Long?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    value?.let { gen?.writeString(it.toString()) }
  }

  override fun createContextual(
    prov: SerializerProvider?,
    property: BeanProperty?
  ): JsonSerializer<*>? {
    return property?.let { p ->
      val ref: BigIntegerAsString? = p.getAnnotation(BigIntegerAsString::class.java)
      if (null != ref && p.type.rawClass == Long::class.java) this
      else prov?.findValueSerializer(property.type, property)
    } ?: prov?.findNullValueSerializer(property)
  }
}
