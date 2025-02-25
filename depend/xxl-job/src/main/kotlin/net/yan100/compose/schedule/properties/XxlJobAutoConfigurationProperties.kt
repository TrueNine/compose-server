package net.yan100.compose.schedule.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

private const val PREFIX = "compose.schedule"

/**
 * xxl-job 配置项
 *
 * @author t_teng
 * @since 2023-03-25
 */
@Deprecated("")
@ConfigurationProperties(prefix = "$PREFIX")
data class XxlJobAutoConfigurationProperties(
  var adminAddress: String = "http://localhost/xxl-job-admin",
  var accessToken: String = "default_token",
  @NestedConfigurationProperty
  var executor: XxlJobExecutorAutoConfigurationProperties =
    XxlJobExecutorAutoConfigurationProperties(),
)
