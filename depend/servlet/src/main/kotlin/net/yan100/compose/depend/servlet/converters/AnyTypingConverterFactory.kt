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
package net.yan100.compose.depend.servlet.converters

import net.yan100.compose.core.slf4j
import net.yan100.compose.core.typing.AnyTyping
import net.yan100.compose.core.typing.IntTyping
import net.yan100.compose.core.typing.StringTyping
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory
import java.util.concurrent.ConcurrentHashMap

@Suppress("DEPRECATION_ERROR")
private val log = slf4j<AnyTypingConverterFactory>()

@Deprecated(message = "API 负担过大", level = DeprecationLevel.ERROR)
open class AnyTypingConverterFactory : ConverterFactory<String?, AnyTyping?> {
  companion object {
    @JvmStatic
    private val converters = ConcurrentHashMap<Class<*>, Converter<String?, AnyTyping?>>()
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : AnyTyping?> getConverter(targetType: Class<T>): Converter<String?, T> {
    return converters[targetType].let {
      it ?: AnyTypingConverter(targetType).also { addedConverter ->
        log.trace("inject any typing converter, the target class: {}", targetType)
        converters[targetType] = addedConverter
      }
    } as Converter<String?, T>
  }

  private inner class AnyTypingConverter(
    targetClass: Class<out AnyTyping?>,
  ) : Converter<String?, AnyTyping?> {
    private val isString = StringTyping::class.java.isAssignableFrom(targetClass)
    private val isInt = IntTyping::class.java.isAssignableFrom(targetClass)
    private val valueMappingMap = targetClass.enumConstants.associateBy {
      it?.value
    }
    private val nameMappingMap = targetClass.enumConstants.associateBy {
      (it as Enum<*>).name
    }
    private val ordinalMappingMap = targetClass.enumConstants.associateBy {
      (it as Enum<*>).ordinal
    }

    override fun convert(source: String): AnyTyping? {
      if (source.isBlank()) return null
      val ordinalOrTypeInt = source.toIntOrNull()
      return if (ordinalOrTypeInt != null) {
        if (isInt) valueMappingMap[source]
        else ordinalMappingMap[ordinalOrTypeInt]
      } else {
        if (isString) valueMappingMap[source]
        else nameMappingMap[source]
      }
    }
  }
}
