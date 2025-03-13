package net.yan100.compose.oss.minio.properties

import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.oss"

/**
 * ## minio 配置属性
 * @param endpoint 连接 url
 * @param exposedBaseUrl 暴露的 baseUrl
 * @param endpointPort 连接端口
 * @param accessKey accessKey
 * @param secretKey secretKey
 * @param enableHttps 是否启用 https
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@ConfigurationProperties(prefix = "compose.oss.minio")
data class MinioProperties(
  var endpoint: String = "127.0.0.1",
  var endpointPort: Int = 9000,
  var enableHttps: Boolean = false,
  var exposedBaseUrl: String? = null,
  var accessKey: String? = null,
  var secretKey: String? = null,
)
