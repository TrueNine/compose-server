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
package net.yan100.compose.rds.core.typing.userinfo

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.typing.IntTyping

enum class DegreeTyping(v: Int, val level: Int) : IntTyping {
  @Schema(title = "文盲")
  NONE(0, 0),

  @Schema(title = "小学")
  MIN(1, 1),

  @Schema(title = "初中")
  HALF(2, 2),

  @Schema(title = "中专")
  HALF_TECH(8, 3),

  @Schema(title = "高中")
  HEIGHT(3, 4),

  @Schema(title = "大专")
  HEIGHT_TECH(9, 5),

  @Schema(title = "本科")
  BIG(4, 6),

  @Schema(title = "研究生")
  DISCOVERY(5, 7),

  @Schema(title = "博士")
  EXPERT(6, 8),

  @Schema(title = "博士后")
  AFTER_EXPERT(7, 9),

  @Schema(title = "其他")
  OTHER(9999, -1);

  @JsonValue
  override val value = v

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = DegreeTyping.entries.find { it.value == v }
  }
}
