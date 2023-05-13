package net.yan100.compose.schedule.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * xxl-job 配置项
 *
 * @author t_teng
 * @since 2023-03-25
 */
@Deprecated
@Data
@ConfigurationProperties(prefix = "compose.schedule.xxl-job")
public class XxlJobAutoConfigurationProperties {
  String adminAddress = "http://localhost/xxl-job-admin";
  String accessToken = "default_token";
}


