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
package net.yan100.compose.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.core"

/**
 * # snowflake id 生成器自动配置
 * @author TrueNine
 * @since 2023-04-01
 */
@ConfigurationProperties(prefix = "$PREFIX.snowflake")
data class SnowflakeProperties(
  var workId: Long = 1L,
  var dataCenterId: Long = 2L,
  var sequence: Long = 3L,
  var startTimeStamp: Long = 100000L
)
