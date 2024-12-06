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
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.typing.IntTyping

@Schema(title = "证件印面类型")
enum class CertPointTyping(private val v: Int) : IntTyping {
  @Schema(title = "无要求")
  NONE(0),

  @Schema(title = "正面")
  HEADS(1),

  @Schema(title = "反面")
  TAILS(2),

  @Schema(title = "双面")
  DOUBLE(3),

  @Schema(title = "所有")
  ALL(4),

  @Schema(title = "所有内容")
  ALL_CONTENT(5),

  @Schema(title = "完整的", description = "针对于视频，音频等等……")
  INTACT(6);

  @JsonValue
  override val value: Int = v

  companion object {
    @JvmStatic
    fun findVal(value: Int?) = entries.find { value == it.v }
  }
}
