package net.yan100.compose.rds.core.typing

import net.yan100.compose.core.typing.IntTyping
import net.yan100.compose.rds.core.typing.OrderStatusTyping.entries
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * # 订单流转状态
 *
 * @author TrueNine
 * @since 2023-05-04
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class OrderStatusTyping(private val orderType: Int) : IntTyping {
  /** 预付款 */
  @EnumItem(ordinal = 1001) PRE_PAY(1001),

  /** 取消支付 */
  @EnumItem(ordinal = 1002) CANCEL_PAY(1002),

  /** 已付款 */
  @EnumItem(ordinal = 2001) PAID(2001),

  /** 已退款 */
  @EnumItem(ordinal = 2002) REFUNDED(2002),

  /** 订单已取消 */
  @EnumItem(ordinal = 2003) CANCEL(2003),

  /** 订单已完成 */
  @EnumItem(ordinal = 2023) COMPLETED(2023),

  /** 预退款 */
  @EnumItem(ordinal = 4001) PRE_REFUND(4001),

  /** 支付成功，但业务出现异常 */
  PAY_SUCCESS_BIZ_FAILED(5002);

  override val value: Int = orderType

  companion object {
    @JvmStatic fun findVal(v: Int?) = entries.find { it.orderType == v }

    @JvmStatic fun get(v: Int?) = findVal(v)
  }
}
