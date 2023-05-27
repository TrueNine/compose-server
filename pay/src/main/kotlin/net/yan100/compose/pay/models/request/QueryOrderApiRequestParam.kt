package net.yan100.compose.pay.models.request

import jakarta.validation.constraints.Size


open class QueryOrderApiRequestParam {
  open var orderId: @Size(min = 6, max = 32, message = "参数长度不对") String? = null
  open var orderNo: @Size(min = 1, max = 32, message = "参数长度不对") String? = null
}
