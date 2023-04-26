package com.truenine.component.core.autoconfig

import com.truenine.component.core.id.BizCodeGenerator
import com.truenine.component.core.id.Snowflake
import com.truenine.component.core.id.SynchronizedSimpleBizCodeGenerator
import com.truenine.component.core.id.SynchronizedSimpleSnowflake
import com.truenine.component.core.properties.SnowflakeProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
@EnableConfigurationProperties(SnowflakeProperties::class)
class IdGeneratorAutoConfiguration {

  @Bean(SNOWFLAKE_BEAN_NAME)
  fun snowflake(p: SnowflakeProperties): Snowflake {
    return SynchronizedSimpleSnowflake(
      p.workId,
      p.dataCenterId,
      p.sequence,
      p.startTimeStamp
    )
  }

  @Bean
  @DependsOn(SNOWFLAKE_BEAN_NAME)
  fun bizCode(snowflake: Snowflake): BizCodeGenerator {
    return SynchronizedSimpleBizCodeGenerator(snowflake)
  }

  companion object {
    const val SNOWFLAKE_BEAN_NAME = "snowflake_id_Generate"
  }
}
