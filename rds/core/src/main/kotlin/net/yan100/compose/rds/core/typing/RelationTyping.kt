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
import net.yan100.compose.rds.core.typing.RelationTyping.entries
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * 关系类型
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class RelationTyping(v: Int) : IntTyping {
  /**
   * 无
   */
  @EnumItem(ordinal = 0)
  NONE(0),

  /**
   * 受害者
   */
  @EnumItem(ordinal = 1)
  BENEFICIARIES(1),

  /**
   * 帮凶
   */
  @EnumItem(ordinal = 2)
  PARTICIPATOR(2),

  /**
   * 见证人
   */
  @EnumItem(ordinal = 3)
  WITNESS(3),

  /**
   * 其他
   */
  @EnumItem(ordinal = 9999)
  OTHER(9999);

  override val value: Int = v

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = entries.find { it.value == v }

    @JvmStatic
    operator fun get(v: Int?) = findVal(v)
  }
}
