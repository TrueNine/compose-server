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
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * 用户学历
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class DegreeTyping(v: Int, val level: Int) : IntTyping {
  /**
   * 文盲
   */
  @EnumItem(ordinal = 0)
  NONE(0, 0),

  /**
   * 小学
   */
  @EnumItem(ordinal = 1)
  MIN(1, 1),

  /**
   * 初中
   */
  @EnumItem(ordinal = 2)
  HALF(2, 2),

  /**
   * 中专
   */
  @EnumItem(ordinal = 8)
  HALF_TECH(8, 3),

  /**
   * 高中
   */
  @EnumItem(ordinal = 3)
  HEIGHT(3, 4),

  /**
   * 大专
   */
  @EnumItem(ordinal = 9)
  HEIGHT_TECH(9, 5),

  /**
   * 本科
   */
  @EnumItem(ordinal = 4)
  BIG(4, 6),

  /**
   * 研究生
   */
  @EnumItem(ordinal = 5)
  DISCOVERY(5, 7),

  /**
   * 博士
   */
  @EnumItem(ordinal = 6)
  EXPERT(6, 8),

  /**
   * 博士后
   */
  @EnumItem(ordinal = 7)
  AFTER_EXPERT(7, 9),

  /**
   * 其他
   */
  @EnumItem(ordinal = 9999)
  OTHER(9999, -1);


  override val value = v

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = DegreeTyping.entries.find { it.value == v }

    @JvmStatic
    operator fun get(v: Int) = findVal(v)
  }
}
