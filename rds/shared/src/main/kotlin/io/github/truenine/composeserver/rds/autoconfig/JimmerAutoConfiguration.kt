package io.github.truenine.composeserver.rds.autoconfig

import io.github.truenine.composeserver.generator.ISnowflakeGenerator
import io.github.truenine.composeserver.rds.converters.DurationScalarProvider
import io.github.truenine.composeserver.rds.converters.IntTypingJimmerProvider
import io.github.truenine.composeserver.rds.converters.PeriodScalarProvider
import io.github.truenine.composeserver.rds.converters.StringTypingJimmerProvider
import io.github.truenine.composeserver.rds.generators.JimmerSnowflakeLongIdGenerator
import io.github.truenine.composeserver.rds.generators.JimmerSnowflakeStringIdGenerator
import io.github.truenine.composeserver.rds.interceptors.JimmerEntityDraftInterceptor
import io.github.truenine.composeserver.slf4j
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

  @Bean
  fun stringTypingJimmerProvider(): StringTypingJimmerProvider {
    log.trace("register jimmer string typing enum provider")
    return StringTypingJimmerProvider()
  }

  @Bean
  fun intTypingJimmerProvider(): IntTypingJimmerProvider {
    log.trace("register jimmer int typing enum provider")
    return IntTypingJimmerProvider()
  }

  @Bean
  fun jimmerPeriodScalarProvider(): PeriodScalarProvider {
    return PeriodScalarProvider()
  }

  @Bean
  fun jimmerEntityDraftInterceptor(): JimmerEntityDraftInterceptor {
    return JimmerEntityDraftInterceptor()
  }

  @Bean
  fun jimmerDurationScalarProvider(): DurationScalarProvider {
    return DurationScalarProvider()
  }
}
