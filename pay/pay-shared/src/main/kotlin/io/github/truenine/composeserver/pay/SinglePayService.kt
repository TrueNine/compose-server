package io.github.truenine.composeserver.pay

import io.github.truenine.composeserver.enums.ISO4217
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.math.BigDecimal

/**
 * # Single-configuration payment service
 *
 * @author shanghua
 * @since 2023-05-28
 */
interface SinglePayService {
  @Schema(title = "Create payment order parameters")
  data class CreateMpPayDto(
    /** Amount */
    @field:Schema(title = "Amount") var amount: BigDecimal? = null,
    /** Merchant order ID */
    @field:Schema(title = "Merchant order ID") var customOrderId: String? = null,
    /** WeChat user unique ID */
    var wechatUserOpenId: String? = null,
    /** Order description */
    @field:Schema(title = "Order description") var title: String? = null,
    /** Currency unit */
    @field:Schema(title = "Currency unit") var currency: ISO4217 = ISO4217.CNY,
  )

  @Schema(title = "WeChat mini program payment response")
  data class CreateMpPayVo(
    @field:Schema(title = "32-character random string (up to 32 characters)") var random32String: String? = null,
    @field:Schema(title = "Timestamp in seconds") var iso8601Second: String? = null,
    @field:Schema(title = "Signature method, SHA256-RSA") var signType: String? = "RSA",
    @field:Schema(title = "Signature string") var paySign: String? = null,
  ) {
    @Schema(title = "prepay_id returned by the unified order API", description = "prepay_id parameter value, submit format: prepay_id=***")
    var prePayId: String? = null
      get() = "prepay_id=$field"
      set(v) {
        field = v?.replace("prepay_id=", "")
      }
  }

  /**
   * ## Create mini program payment order
   *
   * @param req request parameters for starting payment
   */
  fun createMpPayOrder(req: CreateMpPayDto): CreateMpPayVo?

  /**
   * ## Query payment order
   *
   * @param findRq parameters for querying payment order
   */
  fun findPayOrder(findRq: FindPayOrderDto): FindPayOrderVo?

  /**
   * ## Refund payment order
   *
   * @param refundAmount refund amount
   * @param totalAmount total amount of the refund order
   * @param currency currency (default CNY)
   */
  fun applyRefundPayOrder(refundAmount: BigDecimal, totalAmount: BigDecimal, currency: ISO4217 = ISO4217.CNY)

  /** ## Receive asynchronous notification callback */
  fun receivePayNotify(
    metaData: String,
    request: HttpServletRequest,
    response: HttpServletResponse,
    lazyCall: (successReq: PaySuccessNotifyVo) -> Unit,
  ): String?
}
