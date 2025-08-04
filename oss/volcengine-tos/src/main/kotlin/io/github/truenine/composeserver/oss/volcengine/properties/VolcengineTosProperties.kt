package io.github.truenine.composeserver.oss.volcengine.properties

import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.oss.volcengine-tos"

/**
 * Volcengine TOS configuration properties
 *
 * @param endpoint TOS service endpoint
 * @param region TOS service region
 * @param accessKey Access key for authentication
 * @param secretKey Secret key for authentication
 * @param sessionToken Session token for temporary credentials
 * @param exposedBaseUrl Public base URL for object access
 * @param enableSsl Enable SSL/TLS connection
 * @param connectionTimeout Connection timeout
 * @param socketTimeout Socket timeout
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
  var endpoint: String? = null,
  var region: String? = null,
  var accessKey: String? = null,
  var secretKey: String? = null,
  var sessionToken: String? = null,
  var exposedBaseUrl: String? = null,
  var enableSsl: Boolean = true,
  var connectionTimeout: Duration = Duration.ofSeconds(30),
  var socketTimeout: Duration = Duration.ofMinutes(5),
  var maxConnections: Int = 100,
  var maxRetries: Int = 3,
  var enableCrc: Boolean = true,
  var enableLogging: Boolean = false,
  var customDomain: String? = null,
  var enableVirtualHostedStyle: Boolean = true,
)
