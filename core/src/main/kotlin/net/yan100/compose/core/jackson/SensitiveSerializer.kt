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
package net.yan100.compose.core.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import net.yan100.compose.core.annotations.SensitiveRef
import net.yan100.compose.core.annotations.SensitiveRef.Strategy

/**
 * jackson json 脱敏属性策略配置
 *
 * @author TrueNine
 * @since 2023-02-20
 */
class SensitiveSerializer : JsonSerializer<String>(), ContextualSerializer {

  private lateinit var strategy: Strategy

  override fun serialize(value: String?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    value?.let { gen?.writeString(strategy.desensitizeSerializer().invoke(it)) }
  }

  override fun createContextual(prov: SerializerProvider?, property: BeanProperty?): JsonSerializer<*>? {
    val ref = property?.getAnnotation(SensitiveRef::class.java)
    return ref?.value?.let {
      strategy = it
      return this
    } ?: prov?.findValueSerializer(property?.type, property)
  }
}
