package io.github.truenine.composeserver.depend.servlet.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.converter.StringHttpMessageConverter
import kotlin.reflect.KClass

/**
 * spring web mvc http servlet configuration properties
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.DEPEND_SERVLET)
data class ServletWebApplicationProperties(
  var allowConverters: MutableList<String> = mutableListOf("getDocumentation", "swaggerResources", "openapiJson"),
  var allowConverterClasses: MutableList<KClass<*>> = mutableListOf(StringHttpMessageConverter::class),
)
