package net.yan100.compose.pay.models.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal


open class CreateOrderApiRequestParam {
  /**
   * 商户自定义订单号
   */
  @Schema(title = "商户自定义订单号")
  @NotBlank(message = "商户订单号不能为空")
  @Size(max = 32, min = 6, message = "商户订单号长度不符合规范")
  open var customOrderId: String? = null

  /**
   * 金额
   */
  @Schema(title = "金额")
  @NotNull(message = "金额不能为空")
  open var amount: BigDecimal? = null

  /**
   * 微信用户唯一 ID
   */
  @NotNull(message = "用户Id")
  @Size(max = 127, min = 1, message = "用户标识长度不符合规范")
  open var wechatUserOpenId: String? = null

  /**
   * 标题
   */
  @Schema(title = "标题")
  @NotNull(message = "订单标题不能为空")
  @Size(max = 127, min = 1, message = "订单标题长度不符合规范")
  open var title: String? = null
}
