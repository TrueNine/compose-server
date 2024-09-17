/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 * 
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
*/
package net.yan100.compose.schedule.properties

import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.schedule.xxl-job"

/**
 * xxl-job executor 配置项
 *
 * @author t_teng
 * @since 2023-03-25
 */
@Deprecated("不再使用此组件")
@ConfigurationProperties(prefix = "$PREFIX.xxl-job.executor")
data class XxlJobExecutorAutoConfigurationProperties(
  var appName: String? = null,
  var address: String = "127.0.0.1",
  var ip: String = "127.0.0.1",
  var port: Int = -1,
  var logPath: String = ".logs/xxl-job",
  var logRetentionDays: Int = 30,
)
