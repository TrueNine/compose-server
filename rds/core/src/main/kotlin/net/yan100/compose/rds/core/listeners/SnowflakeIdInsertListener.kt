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
package net.yan100.compose.rds.core.listeners

import jakarta.annotation.Resource
import jakarta.persistence.PrePersist
import net.yan100.compose.core.generator.ISnowflakeGenerator
import net.yan100.compose.core.slf4j
import net.yan100.compose.rds.core.entities.IAnyEntity
import org.springframework.stereotype.Component

private val log = slf4j<SnowflakeIdInsertListener>()

@Component
class SnowflakeIdInsertListener {
  private lateinit var internalSnowflake: ISnowflakeGenerator
  var snowflake: ISnowflakeGenerator
    @Resource
    set(v) {
      log.trace("注册 id 生成器监听器: {}", v)
      internalSnowflake = v
    }
    get() = internalSnowflake

  @PrePersist
  fun insertId(data: Any?) {
    log.trace("开始执行 id 生成 data: {}", data)
    if (data is IAnyEntity && data.isNew) {
      data.id = snowflake.nextString()
    }
  }
}
