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
package net.yan100.compose.rds.core.typing

import net.yan100.compose.core.typing.IntTyping
import net.yan100.compose.rds.core.typing.AuditTyping.entries
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * 审核状态
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class AuditTyping(v: Int) : IntTyping {
  /**
   * 未审核
   */
  @EnumItem(ordinal = 0)
  NONE(0),

  /**
   * 分配给审核员，或已经分配正在被处理
   */
  @EnumItem(ordinal = 1)
  ASSIGNED(1),

  /**
   * 审核通过
   */
  @EnumItem(ordinal = 2)
  PASS(2),

  /**
   * 审核未通过
   */
  @EnumItem(ordinal = 3)
  FAIL(3),

  /**
   * 已撤销
   */
  @EnumItem(ordinal = 4)
  CANCEL(4),

  /**
   * 已过期
   */
  @EnumItem(ordinal = 5)
  EXPIRED(5),

  /**
   * 驳回
   */
  @EnumItem(ordinal = 6)
  REJECT(6),

  /**
   * 被隐藏
   */
  @EnumItem(ordinal = 7)
  SHADOW_BAN(7);

  override val value: Int = v

  companion object {
    @JvmStatic
    fun findVal(value: Int?) = entries.find { it.value == value }

    @JvmStatic
    operator fun get(value: Int?) = findVal(value)
  }
}
