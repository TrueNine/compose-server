/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

enum class DegreeTyping(private val v: Int) : IntTyping {
  @Schema(title = "文盲") NONE(0),
  @Schema(title = "小学") MIN(1),
  @Schema(title = "初中") HALF(2),
  @Schema(title = "高中") HEIGHT(3),
  @Schema(title = "本科") BIG(4),
  @Schema(title = "研究生") DISCOVERY(5),
  @Schema(title = "博士") EXPERT(6),
  @Schema(title = "博士后") AFTER_EXPERT(7),
  @Schema(title = "其他") OTHER(9999);

  @JsonValue override val value = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = DegreeTyping.entries.find { it.value == v }
  }
}
