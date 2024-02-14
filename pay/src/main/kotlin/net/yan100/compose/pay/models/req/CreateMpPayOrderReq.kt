package net.yan100.compose.pay.models.req

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import net.yan100.compose.core.lang.ISO4217
import java.math.BigDecimal


class CreateMpPayOrderReq {
    /**
     * 金额
     */
    @Schema(title = "金额")
    @NotNull(message = "金额不能为空")
    var amount: BigDecimal? = null

    /**
     * 商户订单号
     */
    @NotBlank(message = "商户订单号不能为控")
    @Max(value = 32, message = "商户订单号最多32位")
    @Schema(title = "商户订单号")
    var customOrderId: String? = null

    /**
     * 微信用户唯一 ID
     */
    @NotNull(message = "用户Id")
    @Size(max = 127, min = 1, message = "用户标识长度不符合规范")
    var wechatUserOpenId: String? = null

    /**
     * 订单描述
     */
    @Schema(title = "订单描述")
    @NotNull(message = "订单描述不能为空")
    @Size(max = 127, min = 1, message = "订单标题长度太长")
    var title: String? = null

    /**
     * 货币单位
     */
    @NotNull
    @Schema(title = "货币单位")
    var currency: ISO4217 = ISO4217.CNY
}
