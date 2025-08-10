package io.github.truenine.composeserver.oss.minio.autoconfig

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.oss.ObjectStorageService
import io.github.truenine.composeserver.oss.minio.MinioObjectStorageService
import io.github.truenine.composeserver.oss.minio.properties.MinioProperties
import io.github.truenine.composeserver.oss.properties.OssProperties
import io.minio.MinioClient
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Auto configuration for MinIO
 *
 * @author TrueNine
 * @since 2025-01-04
 */
@Configuration
@ConditionalOnClass(MinioClient::class)
@ConditionalOnProperty(prefix = SpringBootConfigurationPropertiesPrefixes.OSS, name = ["provider"], havingValue = "minio")
@EnableConfigurationProperties(MinioProperties::class, OssProperties::class)
class MinioAutoConfiguration {

  companion object {
    private val log = logger<MinioAutoConfiguration>()
  }

  @Bean
  @ConditionalOnMissingBean
  fun minioClient(minioProperties: MinioProperties, ossProperties: OssProperties): MinioClient {
    val endpoint = minioProperties.endpoint ?: ossProperties.endpoint
    val port = minioProperties.port
    val accessKey = minioProperties.accessKey ?: ossProperties.accessKey
    val secretKey = minioProperties.secretKey ?: ossProperties.secretKey

    require(!endpoint.isNullOrBlank()) { "MinIO endpoint is required" }
    require(!accessKey.isNullOrBlank()) { "MinIO access key is required" }
    require(!secretKey.isNullOrBlank()) { "MinIO secret key is required" }

    log.info("Initializing MinIO client with endpoint: $endpoint, port: $port, ssl: ${minioProperties.enableSsl}")

    val httpClient =
      OkHttpClient.Builder()
        .connectTimeout(minioProperties.connectionTimeout.toMillis(), TimeUnit.MILLISECONDS)
        .writeTimeout(minioProperties.writeTimeout.toMillis(), TimeUnit.MILLISECONDS)
        .readTimeout(minioProperties.readTimeout.toMillis(), TimeUnit.MILLISECONDS)
        .build()

    val clientBuilder =
      MinioClient.builder()
        .endpoint(endpoint, port ?: if (minioProperties.enableSsl) 443 else 80, minioProperties.enableSsl)
        .credentials(accessKey, secretKey)
        .httpClient(httpClient)

    minioProperties.region?.let { clientBuilder.region(it) }

    val client = clientBuilder.build()

    // Test connection
    try {
      client.listBuckets()
      log.info("MinIO client connected successfully")
    } catch (e: Exception) {
      log.error("MinIO client connection failed", e)
      throw e
    }

    return client
  }

  @Bean
  @ConditionalOnMissingBean
  fun minioObjectStorageService(minioClient: MinioClient, minioProperties: MinioProperties, ossProperties: OssProperties): ObjectStorageService {
    val exposedBaseUrl = minioProperties.exposedBaseUrl ?: ossProperties.exposedBaseUrl ?: buildDefaultUrl(minioProperties)

    log.info("Creating MinIO ObjectStorageService with exposed URL: $exposedBaseUrl")

    return MinioObjectStorageService(minioClient, exposedBaseUrl)
  }

  private fun buildDefaultUrl(minioProperties: MinioProperties): String {
    val protocol = if (minioProperties.enableSsl) "https" else "http"
    val port = minioProperties.port ?: if (minioProperties.enableSsl) 443 else 80
    val portSuffix = if ((minioProperties.enableSsl && port == 443) || (!minioProperties.enableSsl && port == 80)) "" else ":$port"
    return "$protocol://${minioProperties.endpoint}$portSuffix"
  }
}
