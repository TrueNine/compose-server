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

/**
 * 性别类型
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Schema(title = "性别")
enum class GenderTyping(private val v: Int) : IntTyping {
  /** 男 */
  @Schema(title = "男")
  MAN(1),

  /** 女 */
  @Schema(title = "女")
  WOMAN(0),

  /** 未知 */
  @Schema(title = "未知")
  UNKNOWN(9999);

  @JsonValue
  override val value: Int = v

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = entries.find { it.value == v }
  }
}
