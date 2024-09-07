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
package net.yan100.compose.depend.webservlet.converters

import net.yan100.compose.core.log.slf4j
import net.yan100.compose.core.typing.AnyTyping
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory
import java.util.concurrent.ConcurrentHashMap

private val log = slf4j(AnyTypingConverterFactory::class)

open class AnyTypingConverterFactory : ConverterFactory<String?, AnyTyping?> {
  companion object {
    @JvmStatic private val converters = ConcurrentHashMap<Class<*>, Converter<String?, AnyTyping?>>()
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : AnyTyping?> getConverter(targetType: Class<T>): Converter<String?, T> {
    if (converters[targetType] == null) {
      log.trace("反推枚举转换器，target type = {}", targetType)
      converters[targetType] = AnyTypingConverter(targetType)
    }
    return converters[targetType] as Converter<String?, T>
  }

  private inner class AnyTypingConverter(targetClass: Class<out AnyTyping?>, private val mapping: MutableMap<String, AnyTyping> = mutableMapOf()) :
    Converter<String?, AnyTyping?> {
    init {
      if (targetClass.isEnum) targetClass.enumConstants.filterNotNull().forEach { mapping += it.value.toString() to it }
      else log.error("class: {} 不是枚举类型", targetClass)
    }

    override fun convert(source: String): AnyTyping? {
      log.trace("转换枚举 值 = {}", source)
      return mapping[source]
    }
  }
}
