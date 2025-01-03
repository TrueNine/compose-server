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
package net.yan100.compose.core.generator

import net.yan100.compose.core.datetime
import java.time.format.DateTimeFormatter

class SynchronizedSimpleOrderCodeGenerator(
  private val snowflake: ISnowflakeGenerator
) : IOrderCodeGenerator {

  override fun nextString(): String {
    val dt = datetime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
    val st = snowflake.nextString()
    return "$dt${st.substring(st.length - 4)}"
  }
}
