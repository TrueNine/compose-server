package io.github.truenine.composeserver.autoconfig

import io.github.truenine.composeserver.generator.IOrderCodeGenerator
import io.github.truenine.composeserver.generator.ISnowflakeGenerator
import io.github.truenine.composeserver.generator.SynchronizedSimpleOrderCodeGenerator
import io.github.truenine.composeserver.generator.SynchronizedSimpleSnowflake
import io.github.truenine.composeserver.properties.SnowflakeProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
@EnableConfigurationProperties(SnowflakeProperties::class)
class IdGeneratorAutoConfiguration {

  @Bean(SNOWFLAKE_BEAN_NAME)
  fun snowflake(p: SnowflakeProperties): ISnowflakeGenerator {
    return SynchronizedSimpleSnowflake(p.workId, p.dataCenterId, p.sequence, p.startTimeStamp)
  }

  @Bean
  @DependsOn(SNOWFLAKE_BEAN_NAME)
  fun bizCode(snowflake: ISnowflakeGenerator): IOrderCodeGenerator {
    return SynchronizedSimpleOrderCodeGenerator(snowflake)
  }

  companion object {
    const val SNOWFLAKE_BEAN_NAME = "snowflake_id_Generate"
  }
}
