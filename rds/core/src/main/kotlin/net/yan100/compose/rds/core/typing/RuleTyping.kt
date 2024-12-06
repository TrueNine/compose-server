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
 * ## 规则状态
 */
enum class RuleTyping(v: Int) : IntTyping {
  /**
   * 排除
   */
  EXCLUDE(0),

  /**
   * 包含
   */
  INCLUDE(1),

  /**
   * 固定
   */
  FIXED(2);

  @JsonValue
  override val value: Int = v

  companion object {
    @JvmStatic
    fun findVal(e: Int?): RuleTyping? = entries.find { it.value == e }

    @JvmStatic
    fun get(v: Int?) = findVal(v)
  }
}
