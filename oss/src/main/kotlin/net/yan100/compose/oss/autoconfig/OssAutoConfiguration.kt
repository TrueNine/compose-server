package net.yan100.compose.oss.autoconfig

import io.minio.MinioClient
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.oss.Oss
import net.yan100.compose.oss.minio.MinioClientWrapper
import net.yan100.compose.oss.properties.AliCloudOssProperties
import net.yan100.compose.oss.properties.MinioProperties
import net.yan100.compose.oss.properties.OssProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(
  OssProperties::class, MinioProperties::class, AliCloudOssProperties::class
)
class OssAutoConfiguration {
  private val log = slf4j(this::class)

  companion object {
    const val OSS_BEAN_NAME = "objectStorageService"
    const val MINIO_CLIENT_BEAN_NAME = "minioClient"
  }


  @Bean(name = [MINIO_CLIENT_BEAN_NAME])
  @ConditionalOnProperty(value = ["oss.minio.enable"], havingValue = "true")
  fun minioClient(p: OssProperties): MinioClient {
    log.debug("注册 minio = {}", p.minio)
    return MinioClient.builder()
      .endpoint(p.minio.endpointHost, p.minio.endpointPort, false)
      .credentials(p.minio.accessKey, p.minio.secretKey)
      .build()
  }

  @Bean(name = [OSS_BEAN_NAME])
  fun oss(p: OssProperties, ctx: ApplicationContext): Oss? {
    log.debug("注册 oss 客户端，oss 类型为 = {}", p.type)
    return when (p.type) {
      OssProperties.Type.MINIO -> MinioClientWrapper(ctx.getBean(MinioClient::class.java))
      OssProperties.Type.FILE -> null
      OssProperties.Type.MYSQL_DB -> null
      OssProperties.Type.HUAWEI_CLOUD -> null
      else -> null
    }
  }
}
