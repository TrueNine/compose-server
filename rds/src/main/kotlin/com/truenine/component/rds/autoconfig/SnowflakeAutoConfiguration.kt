package com.truenine.component.rds.autoconfig

import com.truenine.component.core.id.Snowflake
import com.truenine.component.core.id.SynchronizedSimpleSnowflake
import com.truenine.component.depend.webclient.properties.SnowflakeProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(com.truenine.component.depend.webclient.properties.SnowflakeProperties::class)
open class SnowflakeAutoConfiguration {

  @Bean
  open fun snowflake(p: com.truenine.component.depend.webclient.properties.SnowflakeProperties): Snowflake {
    return SynchronizedSimpleSnowflake(
      p.workId,
      p.dataCenterId,
      p.sequence,
      p.startTimeStamp
    )
  }
}
