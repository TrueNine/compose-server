package net.yan100.compose.pay.models.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(title = "创建订单返回")
open class CreateOrderApiResponseResult {
  open var prepayId:
    @NotNull(message = "预支付交易会话标识不能为空")
    @Size(max = 64, min = 1, message = "预支付交易会话标识长度不符合规范")
    String? = null
}
