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

/**
 * ## 用户信息所属的常规血型
 *
 */
enum class BloodTyping(v: Int) : IntTyping {
  /**
   * A型
   */
  A(1),

  /**
   * B型
   */
  B(2),

  /**
   * AB型
   */
  AB(3),

  /**
   * O型
   */
  O(4),

  /**
   * 其他血型
   */
  OTHER(9999);

  @JsonValue
  override val value: Int = v

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = BloodTyping.entries.find { it.value == v }

    @JvmStatic
    fun get(v: Int?) = findVal(v)
  }
}
