package com.truenine.component.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @deprecated 以后将不再支持该雪花算法，应该自己生成
 */
@Data
@Deprecated(since = "0.3.8-SNAPSHOT", forRemoval = true)
@ConfigurationProperties(prefix = "component.rds.snowflake")
public class SnowflakeProperties {
  Long workId = (long) 1;
  Long dataCenterId = (long) 2;
  Long sequence = (long) 3;
  Long startTimeStamp = (long) 100000;
}
