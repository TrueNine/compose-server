package net.yan100.compose.pay.models.response

import java.math.BigDecimal


open class QueryOrderApiResponseResult {
  open var orderId: String? = null
  open var orderNo: String? = null
  open var money: BigDecimal? = null
  open var tradeStatus: String? = null
}
