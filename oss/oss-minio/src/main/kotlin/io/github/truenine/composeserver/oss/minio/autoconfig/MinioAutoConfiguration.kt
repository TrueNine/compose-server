package io.github.truenine.composeserver.oss.minio.autoconfig

import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.oss.IObjectStorageService
import io.github.truenine.composeserver.oss.minio.MinioObjectStorageService
import io.github.truenine.composeserver.oss.minio.properties.MinioProperties
import io.github.truenine.composeserver.oss.properties.OssProperties
import io.minio.MinioClient
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment

/**
 * Autoconfiguration for MinIO
 *
 * This configuration is automatically enabled when MinIO client is present in the classpath. No manual provider configuration is required.
 *
 * Priority: 100 (higher priority than cloud providers)
 *
 * @author TrueNine
 * @since 2025-01-04
 */
@Configuration
@ConditionalOnClass(MinioClient::class)
@EnableConfigurationProperties(MinioProperties::class, OssProperties::class)
@Order(100)
class MinioAutoConfiguration {

  companion object {
    @JvmStatic private val log = logger<MinioAutoConfiguration>()
  }

  @Bean
  @ConditionalOnMissingBean
  fun minioClient(ossProperties: OssProperties, environment: Environment, minioProperties: MinioProperties): MinioClient {
    val endpoint =
      minioProperties.endpoint?.takeIf { it.isNotBlank() }
        ?: ossProperties.endpoint
        ?: MinioProperties.DEFAULT_ENDPOINT.also { it: String -> log.warn("use default endpoint {}", it) }
    require(endpoint.isNotBlank()) { "MinIO endpoint is required" }
    val port = minioProperties.port ?: MinioProperties.DEFAULT_PORT.also { log.warn("use default port: {}", it) }
    val accessKey = minioProperties.accessKey ?: ossProperties.accessKey
    val secretKey = minioProperties.secretKey ?: ossProperties.secretKey
    val enableSsl = endpoint.startsWith("https://") || ossProperties.enableSsl || (minioProperties.enableSsl == true)

    require(!accessKey.isNullOrBlank()) { "MinIO access key is required" }
    require(!secretKey.isNullOrBlank()) { "MinIO secret key is required" }

    log.info("Initializing MinIO client with endpoint: $endpoint, port: $port, ssl: ${minioProperties.enableSsl}")

    val connectTimeout = minioProperties.connectionTimeout ?: OssProperties.DEFAULT_CONNECT_TIMEOUT
    val readConnectTimeout = minioProperties.readTimeout ?: OssProperties.DEFAULT_READ_TIMEOUT
    val writeConnectTimeout = minioProperties.writeTimeout ?: OssProperties.DEFAULT_WRITE_TIMEOUT

    val httpClient =
      OkHttpClient.Builder()
        .connectTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS)
        .writeTimeout(writeConnectTimeout.toMillis(), TimeUnit.MILLISECONDS)
        .readTimeout(readConnectTimeout.toMillis(), TimeUnit.MILLISECONDS)
        .build()

    val clientBuilder = MinioClient.builder().endpoint(endpoint, port, enableSsl).credentials(accessKey, secretKey).httpClient(httpClient)

    minioProperties.region?.let { clientBuilder.region(it) }

    val client = clientBuilder.build()

    // 在测试环境中跳过连接测试
    val isTestEnvironment =
      environment.activeProfiles.contains("test") ||
        environment.getProperty("spring.profiles.active")?.contains("test") == true ||
        System.getProperty("java.class.path")?.contains("test") == true

    if (!isTestEnvironment) {
      // Test connection only in non-test environments
      try {
        client.listBuckets()
        log.info("MinIO client connected successfully")
      } catch (e: Exception) {
        log.error("MinIO client connection failed", e)
        throw e
      }
    } else {
      log.info("MinIO client initialized (connection test skipped in test environment)")
    }

    return client
  }

  @Bean
  @ConditionalOnMissingBean
  fun minioObjectStorageService(minioClient: MinioClient, minioProperties: MinioProperties, ossProperties: OssProperties): IObjectStorageService {
    val exposedBaseUrl = minioProperties.exposedBaseUrl ?: ossProperties.exposedBaseUrl ?: buildDefaultUrl(minioProperties)

    log.info("Creating MinIO IObjectStorageService with exposed URL: $exposedBaseUrl")

    return MinioObjectStorageService(minioClient, exposedBaseUrl)
  }

  private fun buildDefaultUrl(minioProperties: MinioProperties): String {
    val protocol = if (minioProperties.enableSsl == true) "https" else "http"
    val port = minioProperties.port ?: if (minioProperties.enableSsl == true) 443 else 80
    val portSuffix = if ((minioProperties.enableSsl == true && port == 443) || (minioProperties.enableSsl == false && port == 80)) "" else ":$port"
    val cleanEndpoint = minioProperties.endpoint?.removePrefix("http://")?.removePrefix("https://")
    return "$protocol://$cleanEndpoint$portSuffix"
  }
}
