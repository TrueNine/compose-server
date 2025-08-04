package io.github.truenine.composeserver.oss.volcengine.autoconfig

import com.volcengine.tos.TOSV2
import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.oss.ObjectStorageService
import io.github.truenine.composeserver.oss.properties.OssProperties
import io.github.truenine.composeserver.oss.volcengine.VolcengineTosObjectStorageService
import io.github.truenine.composeserver.oss.volcengine.properties.VolcengineTosProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Auto configuration for Volcengine TOS
 *
 * @author TrueNine
 * @since 2025-01-04
 */
@Configuration
@ConditionalOnClass(TOSV2::class)
@ConditionalOnProperty(prefix = "compose.oss", name = ["provider"], havingValue = "volcengine-tos")
@EnableConfigurationProperties(VolcengineTosProperties::class, OssProperties::class)
class VolcengineTosAutoConfiguration {

  companion object {
    @JvmStatic private val log = logger<VolcengineTosAutoConfiguration>()
  }

  @Bean
  @ConditionalOnMissingBean
  fun volcengineTosClient(tosProperties: VolcengineTosProperties, ossProperties: OssProperties): TOSV2 {
    val endpoint = tosProperties.endpoint ?: ossProperties.endpoint
    val region = tosProperties.region ?: ossProperties.region
    val accessKey = tosProperties.accessKey ?: ossProperties.accessKey
    val secretKey = tosProperties.secretKey ?: ossProperties.secretKey

    require(!endpoint.isNullOrBlank()) { "TOS endpoint is required" }
    require(!region.isNullOrBlank()) { "TOS region is required" }
    require(!accessKey.isNullOrBlank()) { "TOS access key is required" }
    require(!secretKey.isNullOrBlank()) { "TOS secret key is required" }

    log.info("Initializing Volcengine TOS client with endpoint: $endpoint, region: $region")

    // TODO: Implement actual TOS client creation
    // For now, return a mock client to allow compilation
    throw NotImplementedError("TOS client creation not implemented yet")
  }

  @Bean
  @ConditionalOnMissingBean
  fun volcengineTosObjectStorageService(tosClient: TOSV2, tosProperties: VolcengineTosProperties, ossProperties: OssProperties): ObjectStorageService {
    val exposedBaseUrl =
      tosProperties.exposedBaseUrl
        ?: ossProperties.exposedBaseUrl
        ?: tosProperties.customDomain
        ?: tosProperties.endpoint
        ?: ossProperties.endpoint
        ?: throw IllegalArgumentException("Exposed base URL is required")

    log.info("Creating Volcengine TOS ObjectStorageService with exposed URL: $exposedBaseUrl")

    return VolcengineTosObjectStorageService(tosClient, exposedBaseUrl)
  }
}
