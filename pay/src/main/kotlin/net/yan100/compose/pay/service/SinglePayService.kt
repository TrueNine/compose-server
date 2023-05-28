package net.yan100.compose.pay.service

import net.yan100.compose.core.lang.ISO4217
import net.yan100.compose.pay.models.request.CreateOrderApiRequestParam
import net.yan100.compose.pay.models.request.FindPayOrderRequestParam
import net.yan100.compose.pay.models.response.CreateOrderApiResponseResult
import net.yan100.compose.pay.models.response.FindPayOrderResponseResult
import java.math.BigDecimal

/**
 * # 单配置支付服务
 *
 * @author shanghua
 * @since 2023-05-28
 */
interface SinglePayService {
  /**
   * ## 小程序拉起支付订单
   * @param createOrderRequestParam 拉起支付参数
   */
  fun pullUpMpPayOrder(createOrderRequestParam: CreateOrderApiRequestParam): CreateOrderApiResponseResult?

  /**
   * ## 查询支付订单
   * @param findRq 查询支付订单参数
   */
  fun findPayOrder(findRq: FindPayOrderRequestParam): FindPayOrderResponseResult?

  /**
   * ## 支付订单退款
   *
   * @param refundAmount 退款金额
   * @param totalAmount 退款单 总金额
   * @param currency 币种 （默认 人民币)
   */
  fun applyRefundPayOrder(
    refundAmount: BigDecimal,
    totalAmount: BigDecimal,
    currency: ISO4217 = ISO4217.CNY
  )
}
