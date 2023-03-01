package io.tn.rds.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tserver.snowflake")
data class SnowflakeProperties(
  var workId: Long = 1,
  var dataCenterId: Long = 2,
  var sequence: Long = 3,
  var startTimeStamp: Long = 100000
)
