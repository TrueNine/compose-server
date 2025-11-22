package io.github.truenine.composeserver.pay

import io.github.truenine.composeserver.enums.ISO4217
import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "Payment success notification callback")
data class PaySuccessNotifyVo(
  @get:Schema(title = "Payment order number") var payCode: String,
  @get:Schema(title = "Merchant order ID") var orderCode: String,
  @get:Schema(title = "Payment response metadata") var meta: String?,
  @get:Schema(title = "Currency") var currency: ISO4217?,
)
