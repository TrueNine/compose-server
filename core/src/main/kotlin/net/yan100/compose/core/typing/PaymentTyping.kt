package net.yan100.compose.core.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

/**
 * # 付款流转类型
 *
 * @author TrueNine
 * @since 2023-05-04
 */
@Schema(title = "支付渠道类型")
enum class PaymentTyping(
  private val channelId: Int
) : IntTyping {
  /**
   * 预支付
   */
  @Schema(title = "预付款")
  PRE_PAY(1001),

  /**
   * 已付款
   */
  @Schema(title = "已付款")
  PAID(2001),

  /**
   * 预退款
   */
  @Schema(title = "预退款")
  PRE_REFUND(4001),

  /**
   * 已退款
   */
  @Schema(title = "已退款")
  REFUNDED(2002),

  /**
   * 已取消
   */
  @Schema(title = "已取消")
  CANCEL(2003);

  @JsonValue
  override fun getValue(): Int = channelId

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = PaymentTyping.values().find { it.channelId == v }
  }
}
