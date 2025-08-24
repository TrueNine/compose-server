package io.github.truenine.composeserver.oss.volcengine.autoconfig

import com.volcengine.tos.TOSClientConfiguration
import com.volcengine.tos.TOSV2
import com.volcengine.tos.TOSV2ClientBuilder
import com.volcengine.tos.auth.StaticCredentials
import com.volcengine.tos.transport.TransportConfig
import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.oss.IObjectStorageService
import io.github.truenine.composeserver.oss.properties.OssProperties
import io.github.truenine.composeserver.oss.volcengine.VolcengineTosObjectStorageService
import io.github.truenine.composeserver.oss.volcengine.properties.VolcengineTosProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment

/**
 * Optimized auto configuration for Volcengine TOS with production-ready defaults
 *
 * This configuration is automatically enabled when Volcengine TOS client is present in the classpath. No manual provider configuration is required.
 *
 * This configuration creates a high-performance TOS client with:
 * - Optimized connection pooling
 * - Proper timeout configuration
 * - Comprehensive logging
 * - Error handling and validation
 * - Support for STS, proxy, and custom domains
 *
 * Priority: 200 (lower priority than MinIO)
 *
 * @author TrueNine
 * @since 2025-08-04
 */
@Configuration
@ConditionalOnClass(TOSV2::class)
@EnableConfigurationProperties(VolcengineTosProperties::class, OssProperties::class)
@Order(200)
class VolcengineTosAutoConfiguration {

  companion object {
    @JvmStatic private val log = logger<VolcengineTosAutoConfiguration>()
  }

  @Bean
  @ConditionalOnMissingBean
  fun volcengineTosClient(tosProperties: VolcengineTosProperties, ossProperties: OssProperties, environment: Environment): TOSV2 {
    log.info("Starting Volcengine TOS client initialization...")

    // Validate properties first
    tosProperties.validate()

    // Resolve configuration with fallback to common OSS properties
    val endpoint = resolveEndpoint(tosProperties, ossProperties)
    val region = resolveRegion(tosProperties, ossProperties)
    val accessKey = resolveAccessKey(tosProperties, ossProperties)
    val secretKey = resolveSecretKey(tosProperties, ossProperties)
    val sessionToken = tosProperties.sessionToken

    // Validate required parameters
    validateRequiredParameters(endpoint, region, accessKey, secretKey)

    // Log configuration summary (without sensitive data)
    logConfigurationSummary(tosProperties, endpoint, region, sessionToken != null)

    log.info("Creating Volcengine TOS client with optimized configuration")

    return try {
      val client =
        if (sessionToken != null) {
          // Use STS credentials with simple builder
          log.debug("Using STS credentials with session token")
          TOSV2ClientBuilder().build(region, endpoint, accessKey, secretKey, sessionToken)
        } else {
          // Use advanced configuration for static credentials
          val transportConfig = buildTransportConfig(tosProperties)
          val clientConfiguration = buildClientConfiguration(tosProperties, endpoint, region, accessKey, secretKey, transportConfig)
          TOSV2ClientBuilder().build(clientConfiguration)
        }

      // 在测试环境中跳过连接测试
      val isTestEnvironment =
        environment.activeProfiles.contains("test") ||
          environment.getProperty("spring.profiles.active")?.contains("test") == true ||
          System.getProperty("java.class.path")?.contains("test") == true

      if (!isTestEnvironment) {
        // Test connection only in non-test environments
        try {
          // Use ListBucketsV2Input for connection test
          val listBucketsInput = com.volcengine.tos.model.bucket.ListBucketsV2Input()
          client.listBuckets(listBucketsInput)
          log.info("✅ Volcengine TOS client connected successfully")
        } catch (e: Exception) {
          log.error("❌ Volcengine TOS client connection failed", e)
          throw e
        }
      } else {
        log.info("✅ Volcengine TOS client initialized (connection test skipped in test environment)")
      }

      client
    } catch (e: Exception) {
      log.error("❌ Failed to initialize Volcengine TOS client: ${e.message}", e)
      throw IllegalStateException("Failed to create TOS client", e)
    }
  }

  @Bean
  @ConditionalOnMissingBean
  fun volcengineTosObjectStorageService(tosClient: TOSV2, tosProperties: VolcengineTosProperties, ossProperties: OssProperties): IObjectStorageService {
    log.info("Creating Volcengine TOS IObjectStorageService...")

    val exposedBaseUrl = resolveExposedBaseUrl(tosProperties, ossProperties)

    log.info("✅ Volcengine TOS IObjectStorageService created with exposed URL: $exposedBaseUrl")

    return VolcengineTosObjectStorageService(tosClient, exposedBaseUrl)
  }

  /** Resolve endpoint with detailed logging */
  private fun resolveEndpoint(tosProperties: VolcengineTosProperties, ossProperties: OssProperties): String {
    val endpoint = tosProperties.endpoint ?: ossProperties.endpoint
    log.debug("Resolved endpoint: $endpoint (from ${if (tosProperties.endpoint != null) "TOS properties" else "OSS properties"})")
    return endpoint ?: throw IllegalArgumentException("TOS endpoint is required")
  }

  /** Resolve region with detailed logging */
  private fun resolveRegion(tosProperties: VolcengineTosProperties, ossProperties: OssProperties): String {
    val region = tosProperties.region ?: ossProperties.region
    log.debug("Resolved region: $region (from ${if (tosProperties.region != null) "TOS properties" else "OSS properties"})")
    return region ?: throw IllegalArgumentException("TOS region is required")
  }

  /** Resolve access key with detailed logging */
  private fun resolveAccessKey(tosProperties: VolcengineTosProperties, ossProperties: OssProperties): String {
    val accessKey = tosProperties.accessKey ?: ossProperties.accessKey
    log.debug("Resolved access key: ${accessKey?.take(4)}*** (from ${if (tosProperties.accessKey != null) "TOS properties" else "OSS properties"})")
    return accessKey ?: throw IllegalArgumentException("TOS access key is required")
  }

  /** Resolve secret key with detailed logging */
  private fun resolveSecretKey(tosProperties: VolcengineTosProperties, ossProperties: OssProperties): String {
    val secretKey = tosProperties.secretKey ?: ossProperties.secretKey
    log.debug("Secret key resolved from ${if (tosProperties.secretKey != null) "TOS properties" else "OSS properties"}")
    return secretKey ?: throw IllegalArgumentException("TOS secret key is required")
  }

  /** Validate required parameters */
  private fun validateRequiredParameters(endpoint: String, region: String, accessKey: String, secretKey: String) {
    require(endpoint.isNotBlank()) { "TOS endpoint cannot be blank" }
    require(region.isNotBlank()) { "TOS region cannot be blank" }
    require(accessKey.isNotBlank()) { "TOS access key cannot be blank" }
    require(secretKey.isNotBlank()) { "TOS secret key cannot be blank" }

    log.debug("✅ All required parameters validated successfully")
  }

  /** Log configuration summary without sensitive information */
  private fun logConfigurationSummary(tosProperties: VolcengineTosProperties, endpoint: String, region: String, hasSts: Boolean) {
    log.info("📋 TOS Client Configuration Summary:")
    log.info("  🌐 Endpoint: $endpoint")
    log.info("  📍 Region: $region")
    log.info("  🔐 Authentication: ${if (hasSts) "STS (temporary credentials)" else "Static credentials"}")
    log.info("  🔒 SSL Enabled: ${tosProperties.enableSsl}")
    log.info("  🔗 Max Connections: ${tosProperties.maxConnections}")
    log.info("  ⏱️ Connect Timeout: ${tosProperties.connectTimeoutMills}ms")
    log.info("  📖 Read Timeout: ${tosProperties.readTimeoutMills}ms")
    log.info("  📝 Write Timeout: ${tosProperties.writeTimeoutMills}ms")
    log.info("  🔄 Max Retries: ${tosProperties.maxRetryCount}")
    log.info("  🌐 DNS Cache: ${if (tosProperties.dnsCacheTimeMinutes > 0) "${tosProperties.dnsCacheTimeMinutes}min" else "disabled"}")
    log.info("  ✅ CRC Check: ${tosProperties.enableCrc}")
    log.info("  🔍 SSL Verify: ${tosProperties.enableVerifySSL}")
    log.info("  📄 Auto Content-Type: ${tosProperties.clientAutoRecognizeContentType}")
    log.info("  📊 Logging: ${tosProperties.enableLogging}")

    if (tosProperties.hasProxyConfiguration()) {
      log.info("  🌐 Proxy: ${tosProperties.proxyHost}:${tosProperties.proxyPort}")
      log.info("  🔐 Proxy Auth: ${tosProperties.hasProxyAuthentication()}")
    }

    if (tosProperties.isCustomDomain) {
      log.info("  🏷️ Custom Domain: ${tosProperties.customDomain}")
    }
  }

  /** Build transport configuration with optimized settings */
  private fun buildTransportConfig(tosProperties: VolcengineTosProperties): TransportConfig {
    log.debug("Building transport configuration...")

    val builder =
      TransportConfig.builder()
        .connectTimeoutMills(tosProperties.connectTimeoutMills)
        .readTimeoutMills(tosProperties.readTimeoutMills)
        .writeTimeoutMills(tosProperties.writeTimeoutMills)
        .idleConnectionTimeMills(tosProperties.idleConnectionTimeMills)
        .maxConnections(tosProperties.maxConnections)
        .maxRetryCount(tosProperties.maxRetryCount)
        .dnsCacheTimeMinutes(tosProperties.dnsCacheTimeMinutes)
        .enableVerifySSL(tosProperties.enableVerifySSL)

    // Configure proxy if specified
    if (tosProperties.hasProxyConfiguration()) {
      log.info("🌐 Configuring proxy: ${tosProperties.proxyHost}:${tosProperties.proxyPort}")
      builder.proxyHost(tosProperties.proxyHost).proxyPort(tosProperties.proxyPort)

      if (tosProperties.hasProxyAuthentication()) {
        log.debug("🔐 Configuring proxy authentication")
        builder.proxyUserName(tosProperties.proxyUserName).proxyPassword(tosProperties.proxyPassword)
      }
    }

    val config = builder.build()
    log.debug("✅ Transport configuration built successfully")
    return config
  }

  /** Build client configuration with all optimizations */
  private fun buildClientConfiguration(
    tosProperties: VolcengineTosProperties,
    endpoint: String,
    region: String,
    accessKey: String,
    secretKey: String,
    transportConfig: TransportConfig,
  ): TOSClientConfiguration {
    log.debug("Building client configuration...")
    log.debug("🔐 Using static credentials")

    val builder =
      TOSClientConfiguration.builder()
        .region(region)
        .endpoint(endpoint)
        .credentials(
          // 使用已弃用的 StaticCredentials，但添加 @Suppress 注解
          @Suppress("DEPRECATION") StaticCredentials(accessKey, secretKey)
        )
        .transportConfig(transportConfig)
        .enableCrc(tosProperties.enableCrc)
        .clientAutoRecognizeContentType(tosProperties.clientAutoRecognizeContentType)
        .isCustomDomain(tosProperties.isCustomDomain)

    // Configure User-Agent
    if (tosProperties.userAgentProductName.isNotBlank()) {
      builder.userAgentProductName(tosProperties.userAgentProductName)
    }
    if (tosProperties.userAgentSoftName.isNotBlank()) {
      builder.userAgentSoftName(tosProperties.userAgentSoftName)
    }
    if (tosProperties.userAgentSoftVersion.isNotBlank()) {
      builder.userAgentSoftVersion(tosProperties.userAgentSoftVersion)
    }
    if (tosProperties.userAgentCustomizedKeyValues.isNotEmpty()) {
      builder.userAgentCustomizedKeyValues(tosProperties.userAgentCustomizedKeyValues)
      log.debug("🏷️ Custom User-Agent values: ${tosProperties.userAgentCustomizedKeyValues}")
    }

    val config = builder.build()
    log.debug("✅ Client configuration built successfully")
    return config
  }

  /** Resolve exposed base URL with fallback logic */
  private fun resolveExposedBaseUrl(tosProperties: VolcengineTosProperties, ossProperties: OssProperties): String {
    val exposedBaseUrl =
      tosProperties.exposedBaseUrl
        ?: ossProperties.exposedBaseUrl
        ?: tosProperties.customDomain
        ?: tosProperties.getEffectiveEndpoint()
        ?: ossProperties.getEffectiveEndpoint()
        ?: throw IllegalArgumentException("Unable to determine exposed base URL")

    log.debug("Resolved exposed base URL: $exposedBaseUrl")
    return exposedBaseUrl
  }
}
