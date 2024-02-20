/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.autoconfig

import net.yan100.compose.core.id.Snowflake
import net.yan100.compose.core.lang.slf4j
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SnowflakeIdGeneratorBean : IdentifierGenerator {
  private val log = slf4j(this::class)

  @Autowired private lateinit var snowflake: Snowflake

  init {
    log.trace("注册 id 生成器 当前未初始")
  }

  companion object {
    const val CLASS_NAME = "net.yan100.compose.rds.autoconfig.SnowflakeIdGeneratorBean"
    const val NAME = "customerSimpleSnowflakeIdGenerator"
  }

  override fun generate(session: SharedSessionContractImplementor?, obj: Any?): Any {
    val snowflakeId = snowflake.nextStringId()
    log.trace("当前生成的 snowflakeId = {}", snowflakeId)
    return snowflakeId
  }
}
