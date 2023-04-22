package com.truenine.component.core.autoconfig

import com.truenine.component.core.id.BizCode
import com.truenine.component.core.id.Snowflake
import com.truenine.component.core.id.SynchronizedSimpleBizCode
import com.truenine.component.core.id.SynchronizedSimpleSnowflake
import com.truenine.component.core.properties.SnowflakeProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(SnowflakeProperties::class)
class IdGeneratorAutoConfiguration {

  @Bean
  fun snowflake(p: SnowflakeProperties): Snowflake {
    return SynchronizedSimpleSnowflake(
      p.workId,
      p.dataCenterId,
      p.sequence,
      p.startTimeStamp
    )
  }

  @Bean
  fun bizCode(snowflake: Snowflake): BizCode {
    return SynchronizedSimpleBizCode(snowflake)
  }
}
