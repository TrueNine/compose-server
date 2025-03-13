package net.yan100.compose.oss.minio.autoconfig

import net.yan100.compose.oss.minio.properties.MinioProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

/**
 * @author TrueNine
 * @since 2022-10-28
 */
@ComponentScan("net.yan100.compose.oss.minio.autoconfig")
@EnableConfigurationProperties(
  MinioProperties::class
)
class AutoConfigEntrance
