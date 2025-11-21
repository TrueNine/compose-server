package io.github.truenine.composeserver.pay

import io.github.truenine.composeserver.datetime
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

// https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_2.shtml
@Schema(title = "Payment order query response")
data class FindPayOrderVo(
  @Schema(title = "Merchant order ID") var customOrderId: String? = null,
  @Schema(title = "Order number", description = "WeChat transactionId") var orderNumber: String? = null,
  @Schema(title = "Amount") var amount: BigDecimal? = null,

  /**
   * Transaction status. Enum values: SUCCESS: payment successful; REFUND: refunded; NOTPAY: not paid; CLOSED: closed; REVOKED: revoked (only returned for code
   * payment); USERPAYING: user is paying (only returned for code payment); PAYERROR: payment failed (only returned for code payment).
   */
  @Schema(title = "Transaction status") var tradeStatus: String? = null,
  @Schema(title = "Transaction status description") var tradeStatusDesc: String? = null,

  /**
   * Transaction type. Enum values: JSAPI: Official Account payment; NATIVE: QR code payment; APP: app payment; MICROPAY: code payment; MWEB: H5 payment;
   * FACEPAY: face payment.
   */
  // TODO Improve this type and convert to enum
  @Schema(title = "Transaction type") var tradeType: String? = null,

  // TODO Convert to enum
  var bankType: String? = null,

  // TODO Additional data, returned as-is in query API and payment notifications; only present when payment is completed
  var attach: String? = null,

  /**
   * Payment completion time, in RFC3339 format
   *
   * Format: yyyy-MM-DDTHH:mm:ss+TIMEZONE
   */
  @Schema(title = "Payment completion time") var paySuccessDatetime: datetime? = null,
  @Schema(title = "Payment API metadata") var meta: Any? = null,
)
