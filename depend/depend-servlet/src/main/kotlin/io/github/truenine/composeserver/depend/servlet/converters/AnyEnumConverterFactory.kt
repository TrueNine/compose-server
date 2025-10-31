package io.github.truenine.composeserver.depend.servlet.converters

import io.github.truenine.composeserver.IAnyEnum
import io.github.truenine.composeserver.IIntEnum
import io.github.truenine.composeserver.IStringEnum
import io.github.truenine.composeserver.slf4j
import java.util.concurrent.ConcurrentHashMap
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory

@Suppress("DEPRECATION_ERROR") private val log = slf4j<AnyEnumConverterFactory>()

@Deprecated(message = "API burden is too high", level = DeprecationLevel.ERROR)
open class AnyEnumConverterFactory : ConverterFactory<String?, IAnyEnum?> {
  companion object {
    @JvmStatic private val converters = ConcurrentHashMap<Class<*>, Converter<String?, IAnyEnum?>>()
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : IAnyEnum?> getConverter(targetType: Class<T>): Converter<String?, T> {
    return converters[targetType].let {
      it
        ?: AnyEnumConverter(targetType).also { addedConverter ->
          log.trace("inject any enums converter, the target class: {}", targetType)
          converters[targetType] = addedConverter
        }
    } as Converter<String?, T>
  }

  private inner class AnyEnumConverter(targetClass: Class<out IAnyEnum?>) : Converter<String?, IAnyEnum?> {
    private val isString = IStringEnum::class.java.isAssignableFrom(targetClass)
    private val isInt = IIntEnum::class.java.isAssignableFrom(targetClass)
    private val valueMappingMap = targetClass.enumConstants.associateBy { it?.value }
    private val nameMappingMap = targetClass.enumConstants.associateBy { (it as Enum<*>).name }
    private val ordinalMappingMap = targetClass.enumConstants.associateBy { (it as Enum<*>).ordinal }

    override fun convert(source: String): IAnyEnum? {
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
