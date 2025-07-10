package net.yan100.compose.autoconfig

import net.yan100.compose.generator.IOrderCodeGenerator
import net.yan100.compose.generator.ISnowflakeGenerator
import net.yan100.compose.generator.SynchronizedSimpleOrderCodeGenerator
import net.yan100.compose.generator.SynchronizedSimpleSnowflake
import net.yan100.compose.properties.SnowflakeProperties
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
