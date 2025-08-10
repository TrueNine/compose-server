package io.github.truenine.composeserver.oss.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Modern OSS configuration properties
 *
 * Contains all common OSS configuration fields and adds provider-specific configuration.
 *
 * @param provider OSS provider type (minio, volcengine-tos, aliyun-oss, etc.)
 * @param endpoint Service endpoint URL
 * @param region Service region
 * @param accessKey Access key for authentication
 * @param secretKey Secret key for authentication
 * @param exposedBaseUrl Public base URL for object access
 * @param enableSsl Enable SSL/TLS connection
 * @param connectionTimeout Connection timeout
 * @param readTimeout Read timeout
 * @param writeTimeout Write timeout
 * @param maxConnections Maximum number of connections
 * @param defaultBucket Default bucket name
 * @param autoCreateBucket Auto create bucket if not exists
 * @param enableVersioning Enable object versioning
 * @param enableLogging Enable request/response logging
 * @author TrueNine
 * @since 2025-01-04
 */
@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.OSS)
data class OssProperties(
  /** OSS provider type (minio, volcengine-tos, aliyun-oss, etc.) */
  var provider: String? = null,

  /** Service endpoint URL */
  var endpoint: String? = null,

  /** Service region */
  var region: String? = null,

  /** Access key for authentication */
  var accessKey: String? = null,

  /** Secret key for authentication */
  var secretKey: String? = null,

  /** Public base URL for object access */
  var exposedBaseUrl: String? = null,

  /** Enable SSL/TLS connection */
  var enableSsl: Boolean = true,

  /** Connection timeout */
  var connectionTimeout: Duration = Duration.ofSeconds(30),

  /** Read timeout */
  var readTimeout: Duration = Duration.ofMinutes(5),

  /** Write timeout */
  var writeTimeout: Duration = Duration.ofMinutes(5),

  /** Maximum number of connections */
  var maxConnections: Int = 100,

  /** Default bucket name */
  var defaultBucket: String? = null,

  /** Auto create bucket if not exists */
  var autoCreateBucket: Boolean = false,

  /** Enable object versioning */
  var enableVersioning: Boolean = false,

  /** Enable request/response logging */
  var enableLogging: Boolean = false,
) {
  /**
   * Validate the configuration properties
   *
   * @throws IllegalArgumentException if required properties are missing or invalid
   */
  fun validate() {
    require(!endpoint.isNullOrBlank()) { "Endpoint cannot be null or blank" }
    require(!accessKey.isNullOrBlank()) { "Access key cannot be null or blank" }
    require(!secretKey.isNullOrBlank()) { "Secret key cannot be null or blank" }
    require(maxConnections > 0) { "Max connections must be positive" }
    require(!connectionTimeout.isNegative) { "Connection timeout cannot be negative" }
    require(!readTimeout.isNegative) { "Read timeout cannot be negative" }
    require(!writeTimeout.isNegative) { "Write timeout cannot be negative" }
  }

  /** Get the effective endpoint URL with protocol */
  fun getEffectiveEndpoint(): String {
    val baseEndpoint = endpoint ?: throw IllegalStateException("Endpoint is not configured")
    return if (baseEndpoint.startsWith("http://") || baseEndpoint.startsWith("https://")) {
      baseEndpoint
    } else {
      val protocol = if (enableSsl) "https" else "http"
      "$protocol://$baseEndpoint"
    }
  }

  /** Get the effective exposed base URL */
  fun getEffectiveExposedBaseUrl(): String {
    return exposedBaseUrl ?: getEffectiveEndpoint()
  }

  override fun toString(): String {
    return "OssProperties(" +
      "provider='$provider', " +
      "endpoint='$endpoint', " +
      "region='$region', " +
      "accessKey='${accessKey?.take(4)}***', " +
      "enableSsl=$enableSsl, " +
      "connectionTimeout=$connectionTimeout, " +
      "readTimeout=$readTimeout, " +
      "writeTimeout=$writeTimeout, " +
      "maxConnections=$maxConnections, " +
      "defaultBucket='$defaultBucket', " +
      "autoCreateBucket=$autoCreateBucket, " +
      "enableVersioning=$enableVersioning, " +
      "enableLogging=$enableLogging" +
      ")"
  }
}
