package net.yan100.compose.core.lang

import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory
import java.util.concurrent.ConcurrentHashMap


interface AnyTyping {
  fun getValue(): Any?
}

/**
 * # 数值型枚举
 */
interface IntTyping : AnyTyping {
  override fun getValue(): Int?
}

/**
 * # 字符型枚举
 */
interface StringTyping : AnyTyping {
  override fun getValue(): String?
}

open class AnyTypingConverterFactory : ConverterFactory<String?, AnyTyping?> {
  companion object {
    @JvmStatic
    private val converters = ConcurrentHashMap<Class<*>, Converter<String?, AnyTyping?>>()

    @JvmStatic
    private val log = slf4j(this::class)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : AnyTyping?> getConverter(targetType: Class<T>): Converter<String?, T> {
    if (converters[targetType] == null) {
      converters[targetType] = AnyTypingConverter(targetType)
    }
    return converters[targetType] as Converter<String?, T>
  }

  private inner class AnyTypingConverter(
    targetClass: Class<out AnyTyping?>,
    private val mapping: MutableMap<String, AnyTyping> = mutableMapOf()
  ) : Converter<String?, AnyTyping?> {
    init {
      if (targetClass.isEnum) {
        targetClass.enumConstants.filterNotNull()
          .forEach {
            mapping += it.getValue().toString() to it
          }
      } else {
        log.error("class: {} 不是枚举类型", targetClass)
      }
    }

    override fun convert(source: String): AnyTyping? {
      return mapping[source]
    }
  }
}
