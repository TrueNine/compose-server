package com.truenine.component.rds.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "component.snowflake")
data class SnowflakeProperties(
  var workId: Long = 1,
  var dataCenterId: Long = 2,
  var sequence: Long = 3,
  var startTimeStamp: Long = 100000
)
