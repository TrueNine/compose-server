package io.github.truenine.composeserver.oss.autoconfig

import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.oss.properties.OssProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(OssProperties::class)
class OssAutoConfiguration {
  companion object {
    @JvmStatic
    private val log = logger<OssAutoConfiguration>()
  }
}
