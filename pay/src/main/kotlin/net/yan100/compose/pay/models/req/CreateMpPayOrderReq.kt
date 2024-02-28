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
package net.yan100.compose.pay.models.req

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import net.yan100.compose.core.typing.ISO4217

class CreateMpPayOrderReq {
  /** 金额 */
  @Schema(title = "金额") @NotNull(message = "金额不能为空") var amount: BigDecimal? = null

  /** 商户订单号 */
  @NotBlank(message = "商户订单号不能为控")
  @Max(value = 32, message = "商户订单号最多32位")
  @Schema(title = "商户订单号")
  var customOrderId: String? = null

  /** 微信用户唯一 ID */
  @NotNull(message = "用户Id")
  @Size(max = 127, min = 1, message = "用户标识长度不符合规范")
  var wechatUserOpenId: String? = null

  /** 订单描述 */
  @Schema(title = "订单描述")
  @NotNull(message = "订单描述不能为空")
  @Size(max = 127, min = 1, message = "订单标题长度太长")
  var title: String? = null

  /** 货币单位 */
  @NotNull @Schema(title = "货币单位") var currency: ISO4217 = ISO4217.CNY
}
