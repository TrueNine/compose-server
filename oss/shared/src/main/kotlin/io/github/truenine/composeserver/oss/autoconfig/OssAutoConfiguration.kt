package io.github.truenine.composeserver.oss.autoconfig

import io.github.truenine.composeserver.oss.properties.OssProperties
import io.github.truenine.composeserver.slf4j
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(OssProperties::class)
class OssAutoConfiguration {
  companion object {
    private val log = slf4j(OssAutoConfiguration::class)
  }
}
