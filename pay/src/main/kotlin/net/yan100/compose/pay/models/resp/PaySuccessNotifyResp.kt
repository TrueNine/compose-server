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
package net.yan100.compose.pay.models.resp

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.typing.ISO4217

@Schema(title = "支付成功通知回调")
class PaySuccessNotifyResp {
  @Schema(title = "币种") var currency: ISO4217? = null

  @Schema(title = "支付订单号") lateinit var payCode: String

  @Schema(title = "商户订单号") lateinit var orderCode: String

  @Schema(title = "支付返回的元数据") var meta: String? = null
}
