package com.truenine.component.core.spring.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.converter.StringHttpMessageConverter

/**
 * spring web mvc http servlet 配置属性
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@ConfigurationProperties(prefix = "component.web-servlet")
data class ServletWebApplicationProperties(
  var allowConverters: Array<String> = arrayOf(
    "getDocumentation",
    "swaggerResources",
    "openapiJson"
  ),
  var allowConverterClasses: Array<Class<*>> = arrayOf(
    StringHttpMessageConverter::class.java
  )
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as ServletWebApplicationProperties
    if (!allowConverters.contentEquals(other.allowConverters)) return false
    return true
  }

  override fun hashCode(): Int {
    return allowConverters.contentHashCode()
  }
}
