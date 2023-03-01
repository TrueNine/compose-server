package com.truenine.component.rds.autoconfig

import com.truenine.component.core.id.SimpleSnowflake
import com.truenine.component.core.id.Snowflake
import com.truenine.component.rds.properties.SnowflakeProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(SnowflakeProperties::class)
open class SnowflakeAutoConfig
  (private val sp: SnowflakeProperties) {

  @Bean
  open fun snowflake(): Snowflake {
    return SimpleSnowflake(
      sp.workId, sp.dataCenterId, sp.sequence, sp.startTimeStamp
    )
  }
}
