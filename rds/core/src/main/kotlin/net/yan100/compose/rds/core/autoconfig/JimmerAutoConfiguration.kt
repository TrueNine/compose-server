package net.yan100.compose.rds.core.autoconfig

import net.yan100.compose.core.generator.ISnowflakeGenerator
import net.yan100.compose.core.slf4j
import net.yan100.compose.rds.jimmer.generators.JimmerSnowflakeLongIdGenerator
import net.yan100.compose.rds.jimmer.generators.JimmerSnowflakeStringIdGenerator
import org.babyfish.jimmer.sql.meta.DatabaseNamingStrategy
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val log = slf4j<JimmerAutoConfiguration>()

@Configuration
class JimmerAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  fun lowercaseDatabaseNamingStrategy(): DatabaseNamingStrategy {
    log.trace("register jimmer lowercaseNamingStrategy::customized")
    return JimmerGenericNamingStrategy()
  }

  @ConditionalOnMissingBean
  @Bean(name = [JimmerSnowflakeStringIdGenerator.JIMMER_SNOWFLAKE_STRING_ID_GENERATOR_NAME])
  fun userSnowflakeStringIdGenerator(snowflake: ISnowflakeGenerator): JimmerSnowflakeStringIdGenerator {
    val e = JimmerSnowflakeStringIdGenerator(snowflake)
    log.trace("register jimmer snowflake string id generator snowflake: {}, jimmer generator: {}", snowflake, e)
    return e
  }

  @Bean(name = [JimmerSnowflakeLongIdGenerator.JIMMER_SNOWFLAKE_LONG_ID_GENERATOR_NAME])
  @ConditionalOnMissingBean
  fun userSnowflakeLongIdGenerator(snowflake: ISnowflakeGenerator): JimmerSnowflakeLongIdGenerator {
    val e = JimmerSnowflakeLongIdGenerator(snowflake)
    log.trace("register jimmer snowflake long id generator snowflake: {}, jimmer generator: {}", snowflake, e)
    return e
  }
}
