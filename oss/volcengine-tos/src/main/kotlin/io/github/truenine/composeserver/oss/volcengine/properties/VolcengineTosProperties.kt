package io.github.truenine.composeserver.oss.volcengine.properties

import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.oss.volcengine-tos"

/**
 * Volcengine TOS configuration properties with optimized defaults
 *
 * Contains all common OSS configuration fields and adds Volcengine TOS specific configuration. All parameters are tuned for production use with reasonable
 * defaults.
 *
 * @param endpoint Service endpoint URL
 * @param region Service region
 * @param accessKey Access key for authentication
 * @param secretKey Secret key for authentication
 * @param sessionToken Session token for temporary credentials (STS)
 * @param exposedBaseUrl Public base URL for object access
 * @param enableSsl Enable SSL/TLS connection
 * @param connectTimeoutMills Connection timeout in milliseconds
 * @param readTimeoutMills Read timeout in milliseconds
 * @param writeTimeoutMills Write timeout in milliseconds
 * @param idleConnectionTimeMills Idle connection timeout in milliseconds
 * @param maxConnections Maximum number of connections in pool
 * @param maxRetryCount Maximum number of retries on failure
 * @param dnsCacheTimeMinutes DNS cache time in minutes
 * @param enableCrc Enable CRC64 checksum validation
 * @param enableVerifySSL Enable SSL certificate verification
 * @param clientAutoRecognizeContentType Auto recognize content type by file extension
 * @param enableLogging Enable detailed request/response logging
 * @param customDomain Custom domain for object access
 * @param isCustomDomain Whether using custom domain (affects bucket name handling)
 * @param userAgentProductName Product name for User-Agent header
 * @param userAgentSoftName Software name for User-Agent header
 * @param userAgentSoftVersion Software version for User-Agent header
 * @param userAgentCustomizedKeyValues Custom key-value pairs for User-Agent
 * @param proxyHost Proxy server host
 * @param proxyPort Proxy server port
 * @param proxyUserName Proxy authentication username
 * @param proxyPassword Proxy authentication password
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

  /** Session token for temporary credentials (STS) */
  val sessionToken: String? = null,

  /** Public base URL for object access */
  val exposedBaseUrl: String? = null,

  /** Enable SSL/TLS connection */
  val enableSsl: Boolean = true,

  // === Timeout Configuration ===
  /** Connection timeout in milliseconds - optimized for production */
  val connectTimeoutMills: Int = 10_000,

  /** Read timeout in milliseconds - balanced for large files */
  val readTimeoutMills: Int = 60_000,

  /** Write timeout in milliseconds - balanced for large uploads */
  val writeTimeoutMills: Int = 60_000,

  /** Idle connection timeout in milliseconds - connection pool optimization */
  val idleConnectionTimeMills: Int = 60_000,

  // === Connection Pool Configuration ===
  /** Maximum number of connections in pool - optimized for high concurrency */
  val maxConnections: Int = 1024,

  /** Maximum number of retries on failure - balanced retry strategy */
  val maxRetryCount: Int = 3,

  /** DNS cache time in minutes - 0 means disabled, positive values enable caching */
  val dnsCacheTimeMinutes: Int = 5,

  // === Feature Toggles ===
  /** Enable CRC64 checksum validation for data integrity */
  val enableCrc: Boolean = true,

  /** Enable SSL certificate verification */
  val enableVerifySSL: Boolean = true,

  /** Auto recognize content type by file extension */
  val clientAutoRecognizeContentType: Boolean = true,

  /** Enable detailed request/response logging */
  val enableLogging: Boolean = false,

  // === Domain Configuration ===
  /** Custom domain for object access */
  val customDomain: String? = null,

  /** Whether using custom domain (affects bucket name handling) */
  val isCustomDomain: Boolean = false,

  // === User-Agent Configuration ===
  /** Product name for User-Agent header */
  val userAgentProductName: String = "ComposeServer",

  /** Software name for User-Agent header */
  val userAgentSoftName: String = "TOS-Client",

  /** Software version for User-Agent header */
  val userAgentSoftVersion: String = "1.0.0",

  /** Custom key-value pairs for User-Agent */
  val userAgentCustomizedKeyValues: Map<String, String> = emptyMap(),

  // === Proxy Configuration ===
  /** Proxy server host */
  val proxyHost: String? = null,

  /** Proxy server port */
  val proxyPort: Int = 0,

  /** Proxy authentication username */
  val proxyUserName: String? = null,

  /** Proxy authentication password */
  val proxyPassword: String? = null,
) {

  /** Validate configuration properties */
  fun validate() {
    require(connectTimeoutMills > 0) { "Connection timeout must be positive" }
    require(readTimeoutMills > 0) { "Read timeout must be positive" }
    require(writeTimeoutMills > 0) { "Write timeout must be positive" }
    require(idleConnectionTimeMills > 0) { "Idle connection timeout must be positive" }
    require(maxConnections > 0) { "Max connections must be positive" }
    require(maxRetryCount >= 0) { "Max retry count cannot be negative" }
    require(dnsCacheTimeMinutes >= 0) { "DNS cache time cannot be negative" }

    if (proxyHost != null) {
      require(proxyPort > 0) { "Proxy port must be positive when proxy host is specified" }
    }
  }

  /** Get effective endpoint with protocol */
  fun getEffectiveEndpoint(): String? {
    return endpoint?.let { ep ->
      if (ep.startsWith("http://") || ep.startsWith("https://")) {
        ep
      } else {
        val protocol = if (enableSsl) "https" else "http"
        "$protocol://$ep"
      }
    }
  }

  /** Check if proxy is configured */
  fun hasProxyConfiguration(): Boolean = !proxyHost.isNullOrBlank() && proxyPort > 0

  /** Check if proxy authentication is configured */
  fun hasProxyAuthentication(): Boolean = hasProxyConfiguration() && !proxyUserName.isNullOrBlank() && !proxyPassword.isNullOrBlank()

  override fun toString(): String {
    return "VolcengineTosProperties(" +
      "endpoint='$endpoint', " +
      "region='$region', " +
      "accessKey='${accessKey?.take(4)}***', " +
      "enableSsl=$enableSsl, " +
      "connectTimeoutMills=$connectTimeoutMills, " +
      "readTimeoutMills=$readTimeoutMills, " +
      "writeTimeoutMills=$writeTimeoutMills, " +
      "maxConnections=$maxConnections, " +
      "maxRetryCount=$maxRetryCount, " +
      "enableCrc=$enableCrc, " +
      "enableLogging=$enableLogging, " +
      "isCustomDomain=$isCustomDomain, " +
      "hasProxy=${hasProxyConfiguration()}" +
      ")"
  }
}
