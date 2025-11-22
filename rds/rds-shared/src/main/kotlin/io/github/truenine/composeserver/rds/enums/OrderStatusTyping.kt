package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * # Order flow status
 *
 * @author TrueNine
 * @since 2023-05-04
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class OrderStatusTyping(private val orderType: Int) : IIntEnum {
  /** Prepaid */
  @EnumItem(ordinal = 1001) PRE_PAY(1001),

  /** Payment cancelled */
  @EnumItem(ordinal = 1002) CANCEL_PAY(1002),

  /** Paid */
  @EnumItem(ordinal = 2001) PAID(2001),

  /** Refunded */
  @EnumItem(ordinal = 2002) REFUNDED(2002),

  /** Order cancelled */
  @EnumItem(ordinal = 2003) CANCEL(2003),

  /** Order completed */
  @EnumItem(ordinal = 2023) COMPLETED(2023),

  /** Pre-refund */
  @EnumItem(ordinal = 4001) PRE_REFUND(4001),

  /** Payment successful, but business exception occurred */
  PAY_SUCCESS_BIZ_FAILED(5002);

  override val value: Int = orderType

  companion object {
    @JvmStatic fun findVal(v: Int?) = entries.find { it.orderType == v }

    @JvmStatic fun get(v: Int?) = findVal(v)
  }
}
