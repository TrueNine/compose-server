/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.oss.autoconfig

import io.minio.MinioClient
import net.yan100.compose.core.slf4j
import net.yan100.compose.oss.Oss
import net.yan100.compose.oss.minio.MinioClientWrapper
import net.yan100.compose.oss.properties.OssProperties
import net.yan100.compose.oss.properties.OssProperties.Type.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
@EnableConfigurationProperties(OssProperties::class)
class OssAutoConfiguration {
  private val log = slf4j(this::class)

  companion object {
    const val OSS_BEAN_NAME = "objectStorageService"
    const val MINIO_CLIENT_BEAN_NAME = "minioClient"
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
