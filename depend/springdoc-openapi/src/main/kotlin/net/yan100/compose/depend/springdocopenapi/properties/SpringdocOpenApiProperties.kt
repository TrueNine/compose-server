package net.yan100.compose.depend.springdocopenapi.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty

private const val PREFIX = "compose.depend.springdoc-open-api"

@ConfigurationPropertiesBinding
@ConfigurationProperties(prefix = PREFIX, ignoreUnknownFields = true)
data class SpringdocOpenApiProperties(
  /** 扫描的包 */
  var scanPackages: MutableList<String> = mutableListOf(),

  /** 扫描的路径 */
  var scanUrlPatterns: List<String> = ArrayList(listOf("/**")),

  /** 分组名称 */
  var group: String = "default",

  /** 开启 jwt 请求头展示 */
  var enableJwtHeader: Boolean = false,

  /** jwt 请求头信息 */
  @NestedConfigurationProperty var jwtHeaderInfo: JwtHeaderInfoProperties = JwtHeaderInfoProperties(),

  /** 类型定义信息 */
  @NestedConfigurationProperty var authorInfo: SwaggerDescInfo = SwaggerDescInfo(),
)
