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
package net.yan100.compose.rds.core.listener

import jakarta.annotation.Resource
import jakarta.persistence.PrePersist
import net.yan100.compose.core.generator.IOrderCodeGenerator
import net.yan100.compose.core.recursionFields
import net.yan100.compose.core.slf4j
import net.yan100.compose.rds.core.annotations.OrderCode
import net.yan100.compose.rds.core.entities.IEntity
import org.springframework.stereotype.Component

private val log = slf4j(BizCodeInsertListener::class)

@Component
class BizCodeInsertListener {
  lateinit var bizCodeGenerator: IOrderCodeGenerator @Resource set

  init {
    log.debug("注册订单编号生成监听器")
  }

  @PrePersist
  fun insertBizCode(data: Any?) {
    data?.let { d ->
      d::class
        .recursionFields(IEntity::class)
        .filter { it.isAnnotationPresent(OrderCode::class.java) }
        .map {
          it.trySetAccessible()
          it.getAnnotation(OrderCode::class.java) to it
        }
        .forEach {
          // 当 为 null 时进行设置
          if (it.second[data] == null) {
            it.second[data] = bizCodeGenerator.nextString()
          }
        }
    }
  }
}
