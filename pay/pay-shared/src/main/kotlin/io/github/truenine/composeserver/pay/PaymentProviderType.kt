package io.github.truenine.composeserver.pay

import com.fasterxml.jackson.annotation.JsonValue
import io.github.truenine.composeserver.IIntEnum
import io.swagger.v3.oas.annotations.media.Schema

/**
 * # Payment flow types
 *
 * @author TrueNine
 * @since 2023-05-04
 */
@Schema(title = "Payment channel type")
enum class PaymentProviderType(private val channelId: Int) : IIntEnum {
  /** Pre payment */
  @Schema(title = "Prepayment") PRE_PAY(1001),

  /** Paid */
  @Schema(title = "Paid") PAID(2001),

  /** Pre refund */
  @Schema(title = "Pre-refund") PRE_REFUND(4001),

  /** Payment successful but business failed */
  @Schema(title = "Payment successful but business failed") PAY_SUCCESS_BIZ_FAILED(5002),

  /** Refunded */
  @Schema(title = "Refunded") REFUNDED(2002),

  /** Cancelled */
  @Schema(title = "Cancelled") CANCEL(2003);

  @JsonValue override val value: Int = channelId

  companion object {
    @JvmStatic fun findVal(v: Int?) = entries.find { it.channelId == v }
  }
}
