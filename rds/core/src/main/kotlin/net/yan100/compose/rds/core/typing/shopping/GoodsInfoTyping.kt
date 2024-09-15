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
package net.yan100.compose.rds.core.typing.shopping

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.typing.IntTyping

/** 商品信息分类 */
@Schema(title = "商品信息分类")
enum class GoodsInfoTyping(private val v: Int) : IntTyping {
  /** 检索类型 */
  @Schema(title = "检索类型")
  RETRIEVAL(0),

  /** 商品单位信息 */
  @Schema(title = "商品单位信息")
  GOODS_UNIT_INFO(1),

  /** 商品单位继承信息 */
  @Schema(title = "商品单位继承信息")
  GOODS_UNIT_EXTEND_INFO(2);

  @JsonValue
  override val value = v

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = entries.find { it.value == v }
  }
}
