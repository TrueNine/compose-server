package io.github.truenine.composeserver.depend.servlet.converters

import io.github.truenine.composeserver.IAnyTyping
import io.github.truenine.composeserver.IIntTyping
import io.github.truenine.composeserver.IStringTyping
import io.github.truenine.composeserver.slf4j
import java.util.concurrent.ConcurrentHashMap
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory

@Suppress("DEPRECATION_ERROR") private val log = slf4j<AnyTypingConverterFactory>()

@Deprecated(message = "API 负担过大", level = DeprecationLevel.ERROR)
open class AnyTypingConverterFactory : ConverterFactory<String?, IAnyTyping?> {
  companion object {
    @JvmStatic private val converters = ConcurrentHashMap<Class<*>, Converter<String?, IAnyTyping?>>()
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : IAnyTyping?> getConverter(targetType: Class<T>): Converter<String?, T> {
    return converters[targetType].let {
      it
        ?: AnyTypingConverter(targetType).also { addedConverter ->
          log.trace("inject any typing converter, the target class: {}", targetType)
          converters[targetType] = addedConverter
        }
    } as Converter<String?, T>
  }

  private inner class AnyTypingConverter(targetClass: Class<out IAnyTyping?>) : Converter<String?, IAnyTyping?> {
    private val isString = IStringTyping::class.java.isAssignableFrom(targetClass)
    private val isInt = IIntTyping::class.java.isAssignableFrom(targetClass)
    private val valueMappingMap = targetClass.enumConstants.associateBy { it?.value }
    private val nameMappingMap = targetClass.enumConstants.associateBy { (it as Enum<*>).name }
    private val ordinalMappingMap = targetClass.enumConstants.associateBy { (it as Enum<*>).ordinal }

    override fun convert(source: String): IAnyTyping? {
      if (source.isBlank()) return null
      val ordinalOrTypeInt = source.toIntOrNull()
      return if (ordinalOrTypeInt != null) {
        if (isInt) valueMappingMap[source] else ordinalMappingMap[ordinalOrTypeInt]
      } else {
        if (isString) valueMappingMap[source] else nameMappingMap[source]
      }
    }
  }
}
