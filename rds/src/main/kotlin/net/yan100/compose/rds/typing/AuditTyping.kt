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

@Schema(title = "审核状态")
enum class AuditTyping(val v: Int) : IntTyping {
  @Schema(title = "未审核") NONE(0),
  @Schema(title = "分配给审核员") ASSIGNED(1),
  @Schema(title = "审核通过") PASS(2),
  @Schema(title = "审核未通过") FAIL(3),
  @Schema(title = "已撤销") CANCEL(4),
  @Schema(title = "已过期") EXPIRED(5),
  @Schema(title = "驳回") REJECT(6);

  @JsonValue override val value: Int = v

  companion object {
    @JvmStatic fun findVal(value: Int?) = entries.find { it.v == value }
  }
}
