package io.github.truenine.composeserver.depend.servlet.properties

import kotlin.reflect.KClass
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.converter.StringHttpMessageConverter

private const val PREFIX = "compose.depend.servlet"

/**
 * spring web mvc http servlet 配置属性
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@ConfigurationProperties(prefix = PREFIX)
data class ServletWebApplicationProperties(
  var allowConverters: MutableList<String> = mutableListOf("getDocumentation", "swaggerResources", "openapiJson"),
  var allowConverterClasses: MutableList<KClass<*>> = mutableListOf(StringHttpMessageConverter::class),
)
