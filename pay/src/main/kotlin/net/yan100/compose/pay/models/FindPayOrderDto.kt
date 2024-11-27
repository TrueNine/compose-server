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
package net.yan100.compose.pay.models

import io.swagger.v3.oas.annotations.media.Schema

/** # 查询支付订单参数 */
data class FindPayOrderDto(
  /** ## 商户订单号 */
  @Schema(title = "商户订单号")
  var merchantOrderId: String? = null,
  /** ## 第三方订单号 */
  @Schema(title = "第三方订单号")
  var bizCode: String? = null,
)
