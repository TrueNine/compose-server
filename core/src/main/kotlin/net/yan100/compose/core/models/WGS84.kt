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
package net.yan100.compose.core.models

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import java.io.Serial
import java.io.Serializable
import java.math.BigDecimal
import net.yan100.compose.core.alias.decimal

/**
 * 地址定位模型
 *
 * @author T_teng
 * @since 2023-04-06
 */
@Schema(title = "位置坐标")
class WGS84() : Serializable {
  @Nullable var x: decimal? = null

  @Nullable var y: decimal? = null

  constructor(x: BigDecimal?, y: BigDecimal?) : this() {
    this.x = x
    this.y = y
  }

  companion object {
    @Serial private val serialVersionUID = 1L
  }
}
