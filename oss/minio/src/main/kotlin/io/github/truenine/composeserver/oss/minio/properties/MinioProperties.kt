package io.github.truenine.composeserver.oss.minio.properties

import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.oss.minio"

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
@ConfigurationProperties(prefix = PREFIX)
data class MinioProperties(
  val endpoint: String = "127.0.0.1",
  val port: Int = 9000,
  val accessKey: String? = null,
  val secretKey: String? = null,
  val exposedBaseUrl: String? = null,
  val enableSsl: Boolean = false,
  val region: String? = null,
  val connectionTimeout: Duration = Duration.ofSeconds(30),
  val writeTimeout: Duration = Duration.ofMinutes(5),
  val readTimeout: Duration = Duration.ofMinutes(5),
  val enableLogging: Boolean = false,
)
