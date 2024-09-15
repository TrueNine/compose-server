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
package net.yan100.compose.pay.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.typing.IntTyping

/**
 * # 付款流转类型
 *
 * @author TrueNine
 * @since 2023-05-04
 */
@Schema(title = "支付渠道类型")
enum class PaymentTyping(private val channelId: Int) : IntTyping {
  /** 预支付 */
  @Schema(title = "预付款")
  PRE_PAY(1001),

  /** 已付款 */
  @Schema(title = "已付款")
  PAID(2001),

  /** 预退款 */
  @Schema(title = "预退款")
  PRE_REFUND(4001),

  /** 支付成功，但出现业务异常 */
  @Schema(title = "支付成功，但出现业务异常")
  PAY_SUCCESS_BIZ_FAILED(5002),

  /** 已退款 */
  @Schema(title = "已退款")
  REFUNDED(2002),

  /** 已取消 */
  @Schema(title = "已取消")
  CANCEL(2003);

  @JsonValue
  override val value: Int = channelId

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = entries.find { it.channelId == v }
  }
}
