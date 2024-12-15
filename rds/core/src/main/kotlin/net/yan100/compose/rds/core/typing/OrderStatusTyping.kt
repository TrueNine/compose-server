/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
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
  /**
   * 预付款
   */
  @EnumItem(ordinal = 1001)
  PRE_PAY(1001),

  /**
   * 取消支付
   */
  @EnumItem(ordinal = 1002)
  CANCEL_PAY(1002),

  /**
   * 已付款
   */
  @EnumItem(ordinal = 2001)
  PAID(2001),

  /**
   * 已退款
   */
  @EnumItem(ordinal = 2002)
  REFUNDED(2002),

  /**
   * 订单已取消
   */
  @EnumItem(ordinal = 2003)
  CANCEL(2003),

  /**
   * 订单已完成
   */
  @EnumItem(ordinal = 2023)
  COMPLETED(2023),

  /**
   * 预退款
   */
  @EnumItem(ordinal = 4001)
  PRE_REFUND(4001),

  /** 支付成功，但业务出现异常 */
  PAY_SUCCESS_BIZ_FAILED(5002);

  override val value: Int = orderType

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = entries.find { it.orderType == v }

    @JvmStatic
    fun get(v: Int?) = findVal(v)
  }
}
