package net.yan100.compose.core.autoconfig

import net.yan100.compose.core.id.BizCodeGenerator
import net.yan100.compose.core.id.Snowflake
import net.yan100.compose.core.id.SynchronizedSimpleBizCodeGenerator
import net.yan100.compose.core.id.SynchronizedSimpleSnowflake
import net.yan100.compose.core.properties.SnowflakeProperties
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
