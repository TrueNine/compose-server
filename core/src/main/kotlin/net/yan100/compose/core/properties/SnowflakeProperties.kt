package net.yan100.compose.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author TrueNine
 * @since 2023-04-01
 */
@ConfigurationProperties(prefix = "compose.core.snowflake")
class SnowflakeProperties {
  var workId = 1L
  var dataCenterId = 2L
  var sequence = 3L
  var startTimeStamp = 100000L
}
