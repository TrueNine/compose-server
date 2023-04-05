package com.truenine.component.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * xxl-job executor 配置项
 *
 * @author t_teng
 * @since 2023-03-25
 */
@Deprecated
@Data
@ConfigurationProperties(prefix = "component.schedule.xxl-job.executor")
public class XxlJobExecutorAutoConfigurationProperties {
  String appName;
  String address = "127.0.0.1";
  String ip = "127.0.0.1";
  Integer port = -1;
  String logPath = ".logs/xxl-job";
  Integer logRetentionDays = 30;
}
