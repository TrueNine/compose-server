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
package net.yan100.compose.pay.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.math.BigDecimal
import net.yan100.compose.core.typing.ISO4217
import net.yan100.compose.pay.models.req.CreateMpPayOrderReq
import net.yan100.compose.pay.models.req.FindPayOrderReq
import net.yan100.compose.pay.models.resp.CreateMpPayOrderResp
import net.yan100.compose.pay.models.resp.FindPayOrderResp
import net.yan100.compose.pay.models.resp.PaySuccessNotifyResp

/**
 * # 单配置支付服务
 *
 * @author shanghua
 * @since 2023-05-28
 */
interface SinglePayService {
  /**
   * ## 小程序拉起支付订单
   *
   * @param req 拉起支付参数
   */
  fun createMpPayOrder(req: CreateMpPayOrderReq): CreateMpPayOrderResp?

  /**
   * ## 查询支付订单
   *
   * @param findRq 查询支付订单参数
   */
  fun findPayOrder(findRq: FindPayOrderReq): FindPayOrderResp?

  /**
   * ## 支付订单退款
   *
   * @param refundAmount 退款金额
   * @param totalAmount 退款单 总金额
   * @param currency 币种 （默认 人民币)
   */
  fun applyRefundPayOrder(refundAmount: BigDecimal, totalAmount: BigDecimal, currency: ISO4217 = ISO4217.CNY)

  /** ## 接受异步通知回调 */
  fun receivePayNotify(
    metaData: String,
    request: HttpServletRequest,
    response: HttpServletResponse,
    lazyCall: (successReq: PaySuccessNotifyResp) -> Unit,
  ): String?
}
