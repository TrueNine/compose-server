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
  var executor: XxlJobExecutorAutoConfigurationProperties = XxlJobExecutorAutoConfigurationProperties()
)
