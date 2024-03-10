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
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(title = "拉起小程序支付微信返回")
class CreateMpPayOrderResp {
  @Schema(title = "32位随机字符串，32位以下")
  @Min(value = 5, message = "随机字符串太短")
  @Max(value = 32, message = "随机字符串不得超过32位")
  var random32String: String? = null

  @Schema(
    title = "统一下单接口返回的 prepay_id",
    description = """
    prepay_id 参数值，提交格式如：prepay_id=***
  """
  )
  var prePayId: String? = null
    get() = "prepay_id=$field"
    set(v) {
      field = v?.replace("prepay_id=", "")
    }

  @Min(value = 0) @Schema(title = "时间戳 秒") var iso8601Second: String? = null

  @Schema(title = "签名方法，SHA256-RSA") var signType: String? = "RSA"

  @Schema(title = "签名字符串") var paySign: String? = null
}
