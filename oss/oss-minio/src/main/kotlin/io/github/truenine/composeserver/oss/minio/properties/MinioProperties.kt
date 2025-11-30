package io.github.truenine.composeserver.oss.minio.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import io.github.truenine.composeserver.oss.properties.OssProperties
import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Modern MinIO configuration properties
 *
 * @param endpoint MinIO server endpoint
 * @param port MinIO server port
 * @param accessKey Access key for authentication
 * @param secretKey Secret key for authentication
 * @param exposedBaseUrl Public base URL for object access
 * @param enableSsl Enable SSL/TLS connection
 * @param region MinIO region (optional)
 * @param connectionTimeout Connection timeout
 * @param writeTimeout Write timeout
 * @param readTimeout Read timeout
 * @param logging Enable request/response logging
 * @author TrueNine
 * @since 2025-01-04
 */
@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.OSS_MINIO)
data class MinioProperties(
  var endpoint: String? = null,
  var port: Int? = null,
  var accessKey: String? = null,
  var secretKey: String? = null,
  var exposedBaseUrl: String? = null,
  var enableSsl: Boolean? = false,
  var region: String? = null,
  var connectionTimeout: Duration? = OssProperties.DEFAULT_CONNECT_TIMEOUT,
  var readTimeout: Duration? = OssProperties.DEFAULT_READ_TIMEOUT,
  var writeTimeout: Duration? = OssProperties.DEFAULT_WRITE_TIMEOUT,
  var logging: Boolean? = false,
) {
  companion object {
    const val DEFAULT_ENDPOINT = "127.0.0.1"
    const val DEFAULT_PORT = 9000
  }
}
