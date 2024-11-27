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

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.yan100.compose.core.typing.ISO4217
import net.yan100.compose.pay.models.FindPayOrderDto
import net.yan100.compose.pay.models.FindPayOrderVo
import net.yan100.compose.pay.models.PaySuccessNotifyVo
import java.math.BigDecimal

/**
 * # 单配置支付服务
 *
 * @author shanghua
 * @since 2023-05-28
 */
interface SinglePayService {
  @Schema(title = "创建支付订单参数")
  data class CreateMpPayDto(
    /** 金额 */
    @Schema(title = "金额")
    var amount: BigDecimal? = null,
    /** 商户订单号 */
    @Schema(title = "商户订单号")
    var customOrderId: String? = null,
    /** 微信用户唯一 ID */
    var wechatUserOpenId: String? = null,
    /** 订单描述 */
    @Schema(title = "订单描述")
    var title: String? = null,
    /** 货币单位 */
    @Schema(title = "货币单位")
    var currency: ISO4217 = ISO4217.CNY
  )

  @Schema(title = "拉起小程序支付微信返回")
  data class CreateMpPayVo(
    @Schema(title = "32位随机字符串，32位以下")
    var random32String: String? = null,
    @Schema(title = "时间戳 秒")
    var iso8601Second: String? = null,
    @Schema(title = "签名方法，SHA256-RSA")
    var signType: String? = "RSA",
    @Schema(title = "签名字符串")
    var paySign: String? = null,
  ) {
    @Schema(
      title = "统一下单接口返回的 prepay_id",
      description = "prepay_id 参数值，提交格式如：prepay_id=***",
    )
    var prePayId: String? = null
      get() = "prepay_id=$field"
      set(v) {
        field = v?.replace("prepay_id=", "")
      }
  }


  /**
   * ## 小程序拉起支付订单
   *
   * @param req 拉起支付参数
   */
  fun createMpPayOrder(req: CreateMpPayDto): CreateMpPayVo?

  /**
   * ## 查询支付订单
   *
   * @param findRq 查询支付订单参数
   */
  fun findPayOrder(findRq: FindPayOrderDto): FindPayOrderVo?

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
    lazyCall: (successReq: PaySuccessNotifyVo) -> Unit,
  ): String?
}
