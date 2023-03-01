package io.tn.core.annotations

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.tn.core.spring.JacksonSensitiveSerializer
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
@Target(AnnotationTarget.FIELD)
@JacksonAnnotationsInside
@JsonInclude
@JsonSerialize(using = JacksonSensitiveSerializer::class)
annotation class SensitiveRef(
  val value: Strategy
) {
  enum class Strategy(private val desensitizeSerializer: (String) -> String) {
    PHONE({ it.replace(Regex("(\\d{3})\\d{4}(\\d{4})"), "$1****$2") }),
    IDCARD({ it.replace(Regex("(\\d{2})[\\w|\\d](\\w{2})"), "$1****$2") }),
    NAME({ it.replace(Regex("(\\S)\\S(\\S*)"), "$1*$2") }),
    ADDRESS({
      it.replace(
        Regex("(\\S{3})\\S{2}(\\S*)\\S{2}"),
        "$1****$2****"
      )
    }),
    PASSWORD({ "****" })
    ;


    open fun desensitizeSerializer(): (String) -> String {
      return desensitizeSerializer
    }
  }
}
