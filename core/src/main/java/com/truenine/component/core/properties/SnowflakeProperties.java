package com.truenine.component.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "component.rds.snowflake")
public class SnowflakeProperties {
  Long workId = (long) 1;
  Long dataCenterId = (long) 2;
  Long sequence = (long) 3;
  Long startTimeStamp = (long) 100000;
}
