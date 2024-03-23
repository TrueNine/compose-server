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
package net.yan100.compose.pay.models.resp

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime

// https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_2.shtml
@Schema(title = "查询支付订单返回")
class FindPayOrderResp {
  @Schema(title = "商户订单号") var customOrderId: String? = null

  @Schema(title = "订单编号", description = "微信 transactionId") var orderNumber: String? = null

  @Schema(title = "金额") var amount: BigDecimal? = null

  /** 交易状态，枚举值： SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回） USERPAYING：用户支付中（仅付款码支付会返回） PAYERROR：支付失败（仅付款码支付会返回） */
  @Schema(title = "交易状态") var tradeStatus: String? = null

  @Schema(title = "交易状态描述") var tradeStatusDesc: String? = null

  /** 交易类型，枚举值： JSAPI：公众号支付 NATIVE：扫码支付 APP：APP支付 MICROPAY：付款码支付 MWEB：H5支付 FACEPAY：刷脸支付 */
  // TODO 完善此类型 转换为枚举值
  @Schema(title = "交易类型") var tradeType: String? = null

  // TODO 转换为枚举值
  var bankType: String? = null

  // TODO 附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用，实际情况下只有支付完成状态才会返回该字段
  var attach: String? = null

  /**
   * 支付完成时间，遵循rfc3339标准格式
   *
   * 格式为 yyyy-MM-DDTHH:mm:ss+TIMEZONE
   */
  @Schema(title = "支付完成时间") var paySuccessDatetime: LocalDateTime? = null

  @Schema(title = "支付接口元数据") var meta: Any? = null
}
