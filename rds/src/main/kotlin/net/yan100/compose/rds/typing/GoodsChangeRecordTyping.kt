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

/**
 * 商品改动类型
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Schema(title = "商品改动类型")
enum class GoodsChangeRecordTyping(private val v: Int) : IntTyping {
  /** 改价格 */
  @Schema(title = "改价格") CHANGE_PRICE(0),

  /** 改标题 */
  @Schema(title = "改标题") CHANGE_TITLE(1);

  @JsonValue override val value = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = entries.find { it.value == v }
  }
}
