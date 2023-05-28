package net.yan100.compose.pay.models.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

/**
 * # 查询支付订单参数
 */
open class FindPayOrderRequestParam {

  /**
   * ## 商户订单号
   */
  @Schema(title = "商户订单号")
  open var merchantOrderId: @Size(min = 6, max = 32, message = "参数长度不对") String? = null

  /**
   * ## 第三方订单号
   */
  @Schema(title = "第三方订单号")
  open var bizCode: @Size(min = 1, max = 32, message = "参数长度不对") String? = null
}
