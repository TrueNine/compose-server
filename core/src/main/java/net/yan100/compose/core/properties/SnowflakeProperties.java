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
    private Long workId = 1L;
    private Long dataCenterId = 2L;
    private Long sequence = 3L;
    private Long startTimeStamp = 100000L;
}
