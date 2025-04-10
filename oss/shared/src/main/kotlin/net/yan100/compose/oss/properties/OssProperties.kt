package net.yan100.compose.oss.properties

import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.oss"

/**
 * oss属性
 *
 * @param baseUrl 连接 url
 * @param exposeBaseUrl 暴露的 baseUrl
 * @param port 连接端口
 * @author TrueNine
 * @since 2022-10-28
 */
@ConfigurationProperties(prefix = PREFIX)
data class OssProperties(
  var baseUrl: String? = null,
  var exposeBaseUrl: String? = null,
  var port: Int? = null,
)
