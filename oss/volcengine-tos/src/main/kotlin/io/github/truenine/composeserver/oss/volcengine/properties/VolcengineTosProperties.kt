package io.github.truenine.composeserver.oss.volcengine.properties

import io.github.truenine.composeserver.oss.properties.BaseOssProperties
import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.oss.volcengine-tos"

/**
 * Volcengine TOS configuration properties
 *
 * Extends BaseOssProperties to inherit common OSS configuration fields and adds Volcengine TOS specific configuration.
 *
 * @author TrueNine
 * @since 2025-08-04
 */
@ConfigurationProperties(prefix = PREFIX)
class VolcengineTosProperties : BaseOssProperties() {
  /** Session token for temporary credentials */
  var sessionToken: String? = null

  /** Socket timeout (maps to readTimeout in base class) */
  var socketTimeout: Duration
    get() = readTimeout
    set(value) {
      readTimeout = value
    }

  /** Maximum number of retries */
  var maxRetries: Int = 3

  /** Enable CRC check */
  var enableCrc: Boolean = true

  /** Custom domain for object access */
  var customDomain: String? = null

  /** Enable virtual hosted style URLs */
  var enableVirtualHostedStyle: Boolean = true

  override fun toString(): String {
    return "VolcengineTosProperties(" +
      "endpoint='$endpoint', " +
      "region='$region', " +
      "accessKey='${accessKey?.take(4)}***', " +
      "sessionToken='${sessionToken?.take(4)}***', " +
      "enableSsl=$enableSsl, " +
      "connectionTimeout=$connectionTimeout, " +
      "socketTimeout=$socketTimeout, " +
      "maxConnections=$maxConnections, " +
      "maxRetries=$maxRetries, " +
      "enableCrc=$enableCrc, " +
      "enableLogging=$enableLogging, " +
      "customDomain='$customDomain', " +
      "enableVirtualHostedStyle=$enableVirtualHostedStyle" +
      ")"
  }
}
