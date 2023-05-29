package net.yan100.compose.pay.models.response

import java.math.BigDecimal


open class FindPayOrderResponseResult {
  open var orderId: String? = null
  open var orderNo: String? = null
  open var amount: BigDecimal? = null
  open var tradeStatus: String? = null
}
