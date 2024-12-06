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
import net.yan100.compose.rds.core.typing.GenderTyping.entries
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType
import org.babyfish.jimmer.sql.EnumType.Strategy

/**
 * 性别类型
 *
 * @author TrueNine
 * @since 2023-04-23
 */

@EnumType(Strategy.ORDINAL)
enum class GenderTyping(private val v: Int) : IntTyping {
  /** 男 */
  @EnumItem(ordinal = 1)
  MAN(1),

  /** 女 */
  @EnumItem(ordinal = 0)
  WOMAN(0),

  /** 未知 */
  @EnumItem(ordinal = 9999)
  UNKNOWN(9999);

  @JsonValue
  override val value: Int = v

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = entries.find { it.value == v }

    @JvmStatic
    operator fun get(v: Int?) = findVal(v)
  }
}
