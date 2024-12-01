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
import jakarta.persistence.PreRemove
import net.yan100.compose.core.slf4j
import net.yan100.compose.rds.core.event.TableRowDeleteSpringEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * 备份删除监听器
 *
 * @author TrueNine
 * @since 2022-12-13
 */
@Component
class TableRowDeletePersistenceListener {
  lateinit var pub: ApplicationEventPublisher @Resource set

  init {
    log.debug("注册jpa数据删除监听器 = {}", this)
  }

  @PreRemove
  fun preRemoveHandle(models: Any?) {
    log.debug("进行数据删除 models = {}", models)
    if (null != models) {
      log.debug("发布删除事件 models = {}", models)
      pub.publishEvent(TableRowDeleteSpringEvent(models))
    }
  }

  companion object {
    private val log = slf4j(TableRowDeletePersistenceListener::class)
  }
}
