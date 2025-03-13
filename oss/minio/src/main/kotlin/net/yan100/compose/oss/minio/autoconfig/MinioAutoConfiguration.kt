package net.yan100.compose.oss.minio.autoconfig

import io.minio.MinioClient
import net.yan100.compose.core.slf4j
import net.yan100.compose.oss.common.Oss
import net.yan100.compose.oss.common.properties.OssProperties
import net.yan100.compose.oss.minio.MinioClientWrapper
import net.yan100.compose.oss.minio.properties.MinioProperties
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
    private val log = slf4j(MinioAutoConfiguration::class)
  }

  @Bean
  @ConditionalOnMissingBean
  fun minioClient(
    ossProperties: OssProperties,
    minioProperties: MinioProperties,
  ): MinioClient {
    val accessUrl = ossProperties.baseUrl ?: minioProperties.endpoint
    val accessPort = ossProperties.port ?: minioProperties.endpointPort
    log.trace("注册 minio accessUrl: {}", accessUrl)
    return MinioClient.builder()
      .endpoint(accessUrl, accessPort, minioProperties.enableHttps)
      .credentials(minioProperties.accessKey, minioProperties.secretKey)
      .build()
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
    return MinioClientWrapper(minioClient, exposeBaseUrl)
  }
}
