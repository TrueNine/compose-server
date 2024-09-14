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
package net.yan100.compose.core.domain

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.decimal
import java.io.Serializable

/**
 * # 地址定位模型
 *
 * @author T_teng
 * @since 2023-04-06
 */
@Schema(title = "位置坐标")
class Coordinate @JvmOverloads constructor(
  var x: decimal? = null,
  var y: decimal? = null
) : Serializable
