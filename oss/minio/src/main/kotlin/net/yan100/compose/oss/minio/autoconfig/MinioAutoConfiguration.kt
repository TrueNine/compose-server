package net.yan100.compose.oss.minio.autoconfig

import io.minio.MinioClient
import net.yan100.compose.oss.Oss
import net.yan100.compose.oss.minio.MinioClientWrapper
import net.yan100.compose.oss.minio.properties.MinioProperties
import net.yan100.compose.oss.properties.OssProperties
import net.yan100.compose.slf4j
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * ## minio 客户端自动配置
 *
 * @author TrueNine
 * @since 2025-03-13
 */
@Configuration
class MinioAutoConfiguration {
  companion object {
    @JvmStatic private val log = slf4j<MinioAutoConfiguration>()
  }

  @Bean
  @ConditionalOnMissingBean
  fun minioClient(
    ossProperties: OssProperties,
    minioProperties: MinioProperties,
  ): MinioClient {
    val accessUrl = ossProperties.baseUrl ?: minioProperties.endpoint
    val accessPort = ossProperties.port ?: minioProperties.endpointPort
    log.trace(
      "register minio client accessUrl: {} , accessPort: {}",
      accessUrl,
      accessPort,
    )

    val minioClient =
      MinioClient.builder()
        .endpoint(accessUrl, accessPort, minioProperties.enableHttps)
        .credentials(minioProperties.accessKey, minioProperties.secretKey)
        .build()

    try {
      minioClient.listBuckets()
      log.trace("minio client connected")
    } catch (e: Exception) {
      log.error("minio client connect failed", e)
      throw e
    }

    return minioClient
  }

  @Bean
  @ConditionalOnBean(MinioClient::class)
  @ConditionalOnMissingBean
  fun minioOssClientWrapper(
    minioClient: MinioClient,
    ossProperties: OssProperties,
    minioProperties: MinioProperties,
  ): Oss {
    val exposeBaseUrl =
      ossProperties.exposeBaseUrl
        ?: minioProperties.exposedBaseUrl
        ?: ossProperties.baseUrl
        ?: minioProperties.endpoint
    log.trace(
      "register minio client wrapper, client: {} , exposeBaseUrl: {}",
      minioClient,
      exposeBaseUrl,
    )
    val clientWrapper = MinioClientWrapper(minioClient, exposeBaseUrl)
    return clientWrapper
  }
}
