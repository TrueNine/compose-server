package com.truenine.component.rds.properties

import lombok.Data
import org.springframework.boot.context.properties.ConfigurationProperties

@Data
@ConfigurationProperties(prefix = "component.rds.snowflake")
public class SnowflakeProperties {
  Long workId = 1;
  Long dataCenterId = 2;
  Long sequence = 3;
  Long startTimeStamp= 100000;
}
