package net.yan100.compose.pay.models.resp

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.ISO4217

@Schema(title = "支付成功通知回调")
class PaySuccessNotifyResp {
    @Schema(title = "币种")
    var currency: ISO4217? = null

    @Schema(title = "支付订单号")
    lateinit var payCode: String

    @Schema(title = "商户订单号")
    lateinit var orderCode: String

    @Schema(title = "支付返回的元数据")
    var meta: String? = null
}
