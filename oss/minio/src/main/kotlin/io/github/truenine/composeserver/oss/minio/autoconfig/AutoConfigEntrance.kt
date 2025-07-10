package io.github.truenine.composeserver.oss.minio.autoconfig

import io.github.truenine.composeserver.oss.minio.properties.MinioProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

/**
 * @author TrueNine
 * @since 2022-10-28
 */
@ComponentScan("io.github.truenine.composeserver.oss.minio.autoconfig") @EnableConfigurationProperties(MinioProperties::class) class AutoConfigEntrance
