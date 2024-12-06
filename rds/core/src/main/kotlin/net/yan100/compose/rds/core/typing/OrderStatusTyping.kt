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

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.typing.IntTyping

/**
 * # 订单流转状态
 *
 * @author TrueNine
 * @since 2023-05-04
 */
@Schema(title = "订单流转状态")
enum class OrderStatusTyping(private val orderType: Int) : IntTyping {
  @Schema(title = "预付款")
  PRE_PAY(1001),

  @Schema(title = "取消支付")
  CANCEL_PAY(1002),

  @Schema(title = "已付款")
  PAID(2001),

  @Schema(title = "订单已取消")
  CANCEL(2003),

  @Schema(title = "已退款")
  REFUNDED(2002),

  @Schema(title = "订单已完成")
  COMPLETED(2023),

  @Schema(title = "预退款")
  PRE_REFUND(4001),

  /** 支付成功，但业务出现异常 */
  @Schema(title = "支付成功，但业务出现异常")
  PAY_SUCCESS_BIZ_FAILED(5002);

  @JsonValue
  override val value: Int = orderType

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = entries.find { it.orderType == v }
  }
}
