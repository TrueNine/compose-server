package io.github.truenine.composeserver.oss.volcengine.properties

import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.oss.volcengine-tos"

/**
 * Volcengine TOS configuration properties
 *
 * Contains all common OSS configuration fields and adds Volcengine TOS specific configuration.
 *
 * @param endpoint Service endpoint URL
 * @param region Service region
 * @param accessKey Access key for authentication
 * @param secretKey Secret key for authentication
 * @param sessionToken Session token for temporary credentials
 * @param exposedBaseUrl Public base URL for object access
 * @param enableSsl Enable SSL/TLS connection
 * @param connectionTimeout Connection timeout
 * @param socketTimeout Socket timeout (maps to readTimeout in base class)
 * @param maxConnections Maximum number of connections
 * @param maxRetries Maximum number of retries
 * @param enableCrc Enable CRC check
 * @param enableLogging Enable request/response logging
 * @param customDomain Custom domain for object access
 * @param enableVirtualHostedStyle Enable virtual hosted style URLs
 * @author TrueNine
 * @since 2025-08-04
 */
@ConfigurationProperties(prefix = PREFIX)
data class VolcengineTosProperties(
  /** Service endpoint URL */
  val endpoint: String? = null,

  /** Service region */
  val region: String? = null,

  /** Access key for authentication */
  val accessKey: String? = null,

  /** Secret key for authentication */
  val secretKey: String? = null,

  /** Session token for temporary credentials */
  val sessionToken: String? = null,

  /** Public base URL for object access */
  val exposedBaseUrl: String? = null,

  /** Enable SSL/TLS connection */
  val enableSsl: Boolean = true,

  /** Connection timeout */
  val connectionTimeout: Duration = Duration.ofSeconds(30),

  /** Socket timeout (maps to readTimeout in base class) */
  val socketTimeout: Duration = Duration.ofMinutes(5),

  /** Maximum number of connections */
  val maxConnections: Int = 100,

  /** Maximum number of retries */
  val maxRetries: Int = 3,

  /** Enable CRC check */
  val enableCrc: Boolean = true,

  /** Enable request/response logging */
  val enableLogging: Boolean = false,

  /** Custom domain for object access */
  val customDomain: String? = null,

  /** Enable virtual hosted style URLs */
  val enableVirtualHostedStyle: Boolean = true,
)
