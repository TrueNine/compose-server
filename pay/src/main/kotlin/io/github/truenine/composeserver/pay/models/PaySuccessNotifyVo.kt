package io.github.truenine.composeserver.pay.models

import io.github.truenine.composeserver.typing.ISO4217
import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "支付成功通知回调")
data class PaySuccessNotifyVo(
  @Schema(title = "支付订单号") var payCode: String,
  @Schema(title = "商户订单号") var orderCode: String,
  @Schema(title = "支付返回的元数据") var meta: String?,
  @Schema(title = "币种") var currency: ISO4217?,
)
