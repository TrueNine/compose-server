package net.yan100.compose.oss.properties

/**
 * minio 配置项
 *
 * @author TrueNine
 * @since 2023-02-21
 */
class MinioProperties {
  var enable = false
  var enableHttps = false
  var endpointHost = "localhost"
  var endpointPort = 9000
  var accessKey: String? = null
  var secretKey: String? = null
}
