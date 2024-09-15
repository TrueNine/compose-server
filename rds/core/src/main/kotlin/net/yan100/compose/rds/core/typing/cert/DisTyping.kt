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
package net.yan100.compose.rds.core.typing.cert

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.typing.IntTyping

/**
 * # 第二代残疾证残疾类别
 *
 * @author TrueNine
 * @since 2023-11-03
 */
enum class DisTyping(private val typ: Int) : IntTyping {
  @Schema(title = "视力")
  EYE(1),
  @Schema(title = "听力")
  EAR(2),
  @Schema(title = "言语")
  MOUTH(3),
  @Schema(title = "肢体")
  BODY(4),
  @Schema(title = "智力")
  IQ(5),
  @Schema(title = "精神")
  NERVE(6),
  @Schema(title = "多重")
  MULTIPLE(7);

  @JsonValue
  override val value = typ

  companion object {
    @JvmStatic
    fun findVal(typ: Int?) = entries.find { it.typ == typ }
  }
}
