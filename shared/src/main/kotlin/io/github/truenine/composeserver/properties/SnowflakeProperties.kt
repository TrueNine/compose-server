package io.github.truenine.composeserver.properties

import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.shared"

/**
 * # snowflake id 生成器自动配置
 *
 * @see io.github.truenine.composeserver.generator.SynchronizedSimpleSnowflake
 * @author TrueNine
 * @since 2023-04-01
 */
@ConfigurationProperties(prefix = "$PREFIX.snowflake")
data class SnowflakeProperties(var workId: Long = 1L, var dataCenterId: Long = 2L, var sequence: Long = 3L, var startTimeStamp: Long = 100000L)
