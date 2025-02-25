package net.yan100.compose.pay.models

import io.swagger.v3.oas.annotations.media.Schema

/** # 查询支付订单参数 */
data class FindPayOrderDto(
  /** ## 商户订单号 */
  @Schema(title = "商户订单号") var merchantOrderId: String? = null,
  /** ## 第三方订单号 */
  @Schema(title = "第三方订单号") var bizCode: String? = null,
)
