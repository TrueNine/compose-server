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
    @JvmStatic
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
    try {
      // 尝试执行一个简单的操作来验证连接
      minioClient.listBuckets()
      log.info("Minio 服务连接成功")
    } catch (e: Exception) {
      log.error("Minio 服务连接失败", e)
      throw RuntimeException("Minio 服务连接失败，应用启动终止", e)
    }

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
