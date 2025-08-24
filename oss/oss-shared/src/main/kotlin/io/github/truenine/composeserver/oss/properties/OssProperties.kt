package io.github.truenine.composeserver.oss.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Modern OSS configuration properties
 *
 * Contains all common OSS configuration fields and adds provider-specific configuration. The OSS provider is now automatically detected based on available
 * dependencies in the classpath.
 *
 * @param provider OSS provider type (minio, volcengine-tos, aliyun-oss, etc.) - DEPRECATED: Auto-detected based on classpath
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
 * @param versioning Enable object versioning
 * @param logging Enable request/response logging
 * @author TrueNine
 * @since 2025-01-04
 */
@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.OSS)
data class OssProperties(
  /**
   * OSS provider type (minio, volcengine-tos, aliyun-oss, etc.)
   *
   * @deprecated Since 2025-08-11. OSS provider is now automatically detected based on available dependencies in the classpath. This property is kept for
   *   backward compatibility and will be removed in a future version.
   */
  @Deprecated(
    message = "OSS provider is now automatically detected based on available dependencies in the classpath",
    replaceWith = ReplaceWith("Auto-detection based on classpath dependencies"),
    level = DeprecationLevel.WARNING,
  )
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
  var enableSsl: Boolean = false,

  /** Connection timeout */
  var connectionTimeout: Duration? = DEFAULT_CONNECT_TIMEOUT,

  /** Read timeout */
  var readTimeout: Duration? = DEFAULT_READ_TIMEOUT,

  /** Write timeout */
  var writeTimeout: Duration? = DEFAULT_WRITE_TIMEOUT,

  /** Maximum number of connections */
  var maxConnections: Int = DEFAULT_MAX_CONNECTIONS,

  /** Default bucket name */
  var defaultBucket: String? = DEFAULT_BUCKET,

  /** Auto create bucket if not exists */
  var autoCreateBucket: Boolean = false,

  /** Enable object versioning */
  var versioning: Boolean = false,

  /** Enable request/response logging */
  var logging: Boolean = false,
) {
  companion object {
    @JvmStatic val DEFAULT_CONNECT_TIMEOUT: Duration = Duration.ofSeconds(5)

    @JvmStatic val DEFAULT_READ_TIMEOUT: Duration = Duration.ofSeconds(3)

    @JvmStatic val DEFAULT_WRITE_TIMEOUT: Duration = Duration.ofSeconds(3)

    const val DEFAULT_MAX_CONNECTIONS = 127

    const val DEFAULT_BUCKET = "attachments"
  }

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
    require(connectionTimeout?.isNegative != true) { "Connection timeout cannot be negative" }
    require(readTimeout?.isNegative != true) { "Read timeout cannot be negative" }
    require(writeTimeout?.isNegative != true) { "Write timeout cannot be negative" }
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

  /** 重写 toString 方法以隐藏敏感信息 */
  override fun toString(): String {
    return "OssProperties(" +
      "provider=${provider?.let { "'$it'" }}, " +
      "endpoint=${endpoint?.let { "'$it'" }}, " +
      "region=${region?.let { "'$it'" }}, " +
      "accessKey=${accessKey?.let { "'${it.take(4)}***'" }}, " +
      "secretKey=${secretKey?.let { "'***'" }}, " +
      "exposedBaseUrl=${exposedBaseUrl?.let { "'$it'" }}, " +
      "enableSsl=$enableSsl, " +
      "connectionTimeout=$connectionTimeout, " +
      "readTimeout=$readTimeout, " +
      "writeTimeout=$writeTimeout, " +
      "maxConnections=$maxConnections, " +
      "defaultBucket=${defaultBucket?.let { "'$it'" }}, " +
      "autoCreateBucket=$autoCreateBucket, " +
      "versioning=$versioning, " +
      "logging=$logging" +
      ")"
  }
}
