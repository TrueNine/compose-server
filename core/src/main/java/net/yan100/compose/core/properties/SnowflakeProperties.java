package net.yan100.compose.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author TrueNine
 * @since 2023-04-01
 */
@Data
@ConfigurationProperties(prefix = "compose.core.snowflake")
public class SnowflakeProperties {
  Long workId = (long) 1;
  Long dataCenterId = (long) 2;
  Long sequence = (long) 3;
  Long startTimeStamp = (long) 100000;
}
