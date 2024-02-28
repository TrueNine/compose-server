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
import net.yan100.compose.core.typing.IntTyping

/**
 * 商品服务类型
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Schema(title = "商品类型")
enum class GoodsTyping(private val v: Int) : IntTyping {
  /** 实体商品 */
  @Schema(title = "实体商品") PHYSICAL_GOODS(0),

  /** 服务商品 */
  @Schema(title = "服务商品") SERVICE_GOODS(1),

  /** 虚拟商品 */
  @Schema(title = "虚拟商品") VIRTUAL_GOODS(2);

  @JsonValue override val value: Int = v

  companion object {
    @JvmStatic fun findVal(v: Int?): GoodsTyping? = entries.find { it.value == v }
  }
}
