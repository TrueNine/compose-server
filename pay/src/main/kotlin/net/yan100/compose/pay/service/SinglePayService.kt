package net.yan100.compose.pay.service

import net.yan100.compose.core.lang.ISO4217
import net.yan100.compose.pay.models.req.FindPayOrderReq
import net.yan100.compose.pay.models.req.CreateMpPayOrderReq
import net.yan100.compose.pay.models.resp.FindPayOrderResp
import net.yan100.compose.pay.models.resp.CreateMpPayOrderResp
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
   * @param req 拉起支付参数
   */
  fun createMpPayOrder(req: CreateMpPayOrderReq): CreateMpPayOrderResp?

  /**
   * ## 查询支付订单
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
  fun applyRefundPayOrder(
    refundAmount: BigDecimal,
    totalAmount: BigDecimal,
    currency: ISO4217 = ISO4217.CNY
  )
}
