package net.yan100.compose.pay.models.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "创建订单返回")
open class CreateOrderApiResponseResult {
  open var prePayId: String? = null
}
