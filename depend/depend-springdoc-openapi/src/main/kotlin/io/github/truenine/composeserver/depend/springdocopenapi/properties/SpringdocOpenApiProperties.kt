package io.github.truenine.composeserver.depend.springdocopenapi.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationPropertiesBinding
@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.DEPEND_SPRINGDOC_OPENAPI, ignoreUnknownFields = true)
data class SpringdocOpenApiProperties(
  /** Packages to scan */
  var scanPackages: MutableList<String> = mutableListOf(),

  /** URL patterns to scan */
  var scanUrlPatterns: List<String> = ArrayList(listOf("/**")),

  /** Group name */
  var group: String = "default",

  /** Enable JWT header display */
  var enableJwtHeader: Boolean = false,

  /** JWT header information */
  @NestedConfigurationProperty var jwtHeaderInfo: JwtHeaderInfoProperties = JwtHeaderInfoProperties(),

  /** Type definition information */
  @NestedConfigurationProperty var authorInfo: SwaggerDescInfo = SwaggerDescInfo(),
)
