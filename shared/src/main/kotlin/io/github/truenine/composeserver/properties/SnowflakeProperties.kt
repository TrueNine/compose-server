package io.github.truenine.composeserver.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * # Snowflake ID generator auto-configuration
 *
 * @see io.github.truenine.composeserver.generator.SynchronizedSimpleSnowflake
 * @author TrueNine
 * @since 2023-04-01
 */
@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.SHARED_SNOWFLAKE)
data class SnowflakeProperties(var workId: Long = 1L, var dataCenterId: Long = 2L, var sequence: Long = 3L, var startTimeStamp: Long = 100000L)
