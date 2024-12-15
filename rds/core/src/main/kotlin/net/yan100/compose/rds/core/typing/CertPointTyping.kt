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
import net.yan100.compose.rds.core.typing.CertPointTyping.entries
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * 证件印面类型
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class CertPointTyping(private val v: Int) : IntTyping {
  /**
   * 无要求
   */
  @EnumItem(ordinal = 0)
  NONE(0),

  /**
   * 正面
   */
  @EnumItem(ordinal = 1)
  HEADS(1),

  /**
   * 反面
   */
  @EnumItem(ordinal = 2)
  TAILS(2),

  /**
   * 双面
   */
  @EnumItem(ordinal = 3)
  DOUBLE(3),

  /**
   * 所有
   */
  @EnumItem(ordinal = 4)
  ALL(4),

  /**
   * 所有内容
   */
  @EnumItem(ordinal = 5)
  ALL_CONTENT(5),

  /**
   * 完整的
   *
   * 针对于视频，音频等等……
   */
  @EnumItem(ordinal = 6)
  INTACT(6);

  override val value: Int = v

  companion object {
    @JvmStatic
    fun findVal(value: Int?) = entries.find { value == it.v }

    @JvmStatic
    operator fun get(v: Int?) = findVal(v)
  }
}
