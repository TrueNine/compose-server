package net.yan100.compose.core.annotations

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import net.yan100.compose.core.autoconfig.LongAsStringSerializer
import net.yan100.compose.core.autoconfig.SensitiveSerializer
import net.yan100.compose.core.autoconfig.StringAsLongDeserializer
import java.lang.annotation.Inherited

/**
 * 脱敏字段检查器，通常标记于字段。
 * 配合 Strategy 里面的常用规则使用
 *
 * @author TrueNine
 * @since 2023-02-19
 */
@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER)
@JacksonAnnotationsInside
@JsonInclude
@JsonSerialize(using = SensitiveSerializer::class)
annotation class SensitiveRef(
  val value: Strategy
)

enum class Strategy(private val desensitizeSerializer: (String) -> String) {
  PHONE({ it.replace(Regex("(\\d{3})\\d{4}(\\d{4})"), "$1****$2") }),
  IDCARD({ it.replace(Regex("(\\d{2})[\\w|\\d](\\w{2})"), "$1****$2") }),
  NAME({ it.replace(Regex("(\\S)\\S(\\S*)"), "$1*$2") }),
  ADDRESS({ it.replace(Regex("(\\S{3})\\S{2}(\\S*)\\S{2}"), "$1****$2****") }),
  PASSWORD({ "****" });

  open fun desensitizeSerializer(): (String) -> String {
    return desensitizeSerializer
  }
}


/**
 * 将 Long 类型序列化为 String，同样反序列化回来也采取 String
 *
 * @author TrueNine
 * @since 2023-02-19
 */
@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@JacksonAnnotationsInside
@JsonInclude
@JsonSerialize(using = LongAsStringSerializer::class)
@JsonDeserialize(using = StringAsLongDeserializer::class)
annotation class BigIntegerAsString
