package net.yan100.compose.core.lang

import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * # 所有类型枚举的抽象接口
 * 实现此接口，以方便其他序列化程序来读取枚举
 * 实现此接口后，需要手动添加一个 findVal 静态方法，提供给 jackson等框架自动调用
 *
 * 由于无法在接口规定静态方法，此算作规约吧。以下为一个枚举类内部的静态方法示例
 *
 * ```kotlin
 * enum class GenderTyping(private val value: Int) {
 *   ...;
 *     @JsonValue
 *     override fun getValue() = this.value
 *     companion object {
 *       @JvmStatic
 *       fun findVal(v: Int?) = GenderTyping.values().find { it.value == v }
 *     }
 * }
 * ```
 * @see [AnyTypingConverterFactory] 此类用于 SpringMVC 的返回以及接收时的转换工作
 * @author TrueNine
 * @since 2023-05-28
 */
interface AnyTyping {
  /**
   * ## 获取枚举对应的实际值
   */
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
    private val log = slf4j(AnyTypingConverterFactory::class)
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
