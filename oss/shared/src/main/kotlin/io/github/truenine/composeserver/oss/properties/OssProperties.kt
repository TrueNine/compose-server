package io.github.truenine.composeserver.oss.properties

import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.oss"

/**
 * Modern OSS configuration properties
 *
 * Extends BaseOssProperties to inherit common OSS configuration fields and adds provider-specific configuration.
 *
 * @param provider OSS provider type (minio, volcengine-tos, aliyun-oss, etc.)
 * @author TrueNine
 * @since 2025-01-04
 */
@ConfigurationProperties(prefix = PREFIX)
class OssProperties : BaseOssProperties() {
  /** OSS provider type (minio, volcengine-tos, aliyun-oss, etc.) */
  var provider: String? = null

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
