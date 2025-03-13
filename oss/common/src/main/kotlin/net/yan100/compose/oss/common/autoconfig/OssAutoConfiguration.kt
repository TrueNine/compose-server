package net.yan100.compose.oss.common.autoconfig

import net.yan100.compose.core.slf4j
import net.yan100.compose.oss.common.properties.OssProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(OssProperties::class)
class OssAutoConfiguration {
  companion object {
    private val log = slf4j(OssAutoConfiguration::class)
  }
}
