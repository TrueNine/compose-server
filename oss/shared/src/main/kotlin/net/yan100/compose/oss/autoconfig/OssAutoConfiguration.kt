package net.yan100.compose.oss.autoconfig

import net.yan100.compose.oss.properties.OssProperties
import net.yan100.compose.slf4j
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(OssProperties::class)
class OssAutoConfiguration {
  companion object {
    private val log = slf4j(OssAutoConfiguration::class)
  }
}
