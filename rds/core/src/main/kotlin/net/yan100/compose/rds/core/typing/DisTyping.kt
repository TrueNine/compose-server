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

import com.fasterxml.jackson.annotation.JsonValue
import net.yan100.compose.core.typing.IntTyping
import net.yan100.compose.rds.core.typing.DisTyping.entries
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * # 第二、三代中国残疾证残疾类别
 *
 * @author TrueNine
 * @since 2023-11-03
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class DisTyping(typ: Int) : IntTyping {
  /**
   * 视力
   */
  @EnumItem(ordinal = 1)
  EYE(1),

  /**
   * 听力
   */
  @EnumItem(ordinal = 2)
  EAR(2),

  /**
   * 言语
   */
  @EnumItem(ordinal = 3)
  MOUTH(3),

  /**
   * 肢体
   */
  @EnumItem(ordinal = 4)
  BODY(4),

  /**
   * 智力
   */
  @EnumItem(ordinal = 5)
  IQ(5),

  /**
   * 精神
   */
  @EnumItem(ordinal = 6)
  NERVE(6),

  /**
   * 多重
   */
  @EnumItem(ordinal = 7)
  MULTIPLE(7);

  @JsonValue
  override val value = typ

  companion object {
    @JvmStatic
    fun findVal(typ: Int?) = entries.find { it.value == typ }

    @JvmStatic
    operator fun get(v: Int?) = findVal(v)
  }
}
