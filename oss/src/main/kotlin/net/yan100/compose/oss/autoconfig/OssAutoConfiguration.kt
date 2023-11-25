package net.yan100.compose.oss.autoconfig

import io.minio.MinioClient
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.oss.Oss
import net.yan100.compose.oss.minio.MinioClientWrapper
import net.yan100.compose.oss.properties.OssProperties
import net.yan100.compose.oss.properties.OssProperties.Type.*
import net.yan100.compose.oss.properties.OssProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn


@Configuration
@EnableConfigurationProperties(
  OssProperties::class
)
class OssAutoConfiguration {
  private val log = slf4j(this::class)

  companion object {
    const val OSS_BEAN_NAME = "objectStorageService"
    const val MINIO_CLIENT_BEAN_NAME = "minioClient"
  }

  @Bean
  fun ossProperty(p: OssProperties): OssProperty {
    val property = OssProperty()
    property.exposeBaseUrl = p.exposeBaseUrl
    property.baseUrl = when (p.type) {
      MINIO -> p.minio.endpointHost + p.minio.endpointPort
      FILE -> null
      MYSQL_DB -> null
      ALI_CLOUD_OSS -> p.aliyun.endpoint
      HUAWEI_CLOUD -> null
      null -> null
    }
    return property
  }


  @Bean(name = [MINIO_CLIENT_BEAN_NAME])
  @ConditionalOnProperty(value = ["compose.oss.minio.enable"], havingValue = "true")
  fun minioClient(p: OssProperties): MinioClient {
    log.debug("注册 minio = {}", p.minio)
    return MinioClient.builder()
      .endpoint(p.minio.endpointHost, p.minio.endpointPort, p.minio.enableHttps)
      .credentials(p.minio.accessKey, p.minio.secretKey)
      .build()
  }

  @Bean(name = [OSS_BEAN_NAME])
  @DependsOn(value = [MINIO_CLIENT_BEAN_NAME])
  fun oss(p: OssProperties, ctx: ApplicationContext): Oss? {
    log.debug("注册 oss 客户端，oss 类型为 = {}", p.type)
    return when (p.type) {
      MINIO -> MinioClientWrapper(ctx.getBean(MinioClient::class.java), p.exposeBaseUrl)
      FILE -> null
      MYSQL_DB -> null
      HUAWEI_CLOUD -> null
      else -> null
    }
  }
}
