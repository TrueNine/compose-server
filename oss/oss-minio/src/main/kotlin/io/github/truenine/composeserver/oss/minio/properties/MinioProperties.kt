package io.github.truenine.composeserver.oss.minio.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
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
 * @param enableLogging Enable request/response logging
 * @author TrueNine
 * @since 2025-01-04
 */
@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.OSS_MINIO)
data class MinioProperties(
  var endpoint: String = "127.0.0.1",
  var port: Int = 9000,
  var accessKey: String? = null,
  var secretKey: String? = null,
  var exposedBaseUrl: String? = null,
  var enableSsl: Boolean = false,
  var region: String? = null,
  var connectionTimeout: Duration = Duration.ofSeconds(30),
  var writeTimeout: Duration = Duration.ofMinutes(5),
  var readTimeout: Duration = Duration.ofMinutes(5),
  var enableLogging: Boolean = false,
)
