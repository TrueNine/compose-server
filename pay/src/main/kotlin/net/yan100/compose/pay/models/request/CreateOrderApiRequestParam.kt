package net.yan100.compose.pay.models.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import lombok.Data
import java.math.BigDecimal


open class CreateOrderApiRequestParam {
  /**
   * 商户订单号
   */
  open var orderId: @NotBlank(message = "商户订单号不能为空") @Size(
    max = 32,
    min = 6,
    message = "商户订单号长度不符合规范"
  ) String? = null

  /**
   * 金额
   */
  open var money: @NotNull(message = "金额不能为空") BigDecimal? = null

  /**
   * 微信用户唯一 ID
   */
  open var openId: @NotNull(message = "用户Id") @Size(
    max = 128,
    min = 1,
    message = "用户标识长度不符合规范"
  ) String? = null

  /**
   * 标题
   */
  open var title: @NotNull(message = "订单标题不能为空") @Size(
    max = 127,
    min = 1,
    message = "订单标题长度不符合规范"
  ) String? = null
}
