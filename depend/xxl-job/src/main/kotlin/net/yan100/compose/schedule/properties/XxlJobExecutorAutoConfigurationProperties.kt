package net.yan100.compose.schedule.properties

private const val PREFIX = "compose.schedule.xxl-job"

/**
 * xxl-job executor 配置项
 *
 * @author t_teng
 * @since 2023-03-25
 */
@Deprecated("不再使用此组件")
data class XxlJobExecutorAutoConfigurationProperties(
  var appName: String? = null,
  var address: String = "127.0.0.1",
  var ip: String = "127.0.0.1",
  var port: Int = -1,
  var logPath: String = ".logs/xxl-job",
  var logRetentionDays: Int = 30,
)
