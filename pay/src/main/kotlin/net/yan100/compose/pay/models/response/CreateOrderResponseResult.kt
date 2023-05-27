package net.yan100.compose.pay.models.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema


@Schema(title = "创建订单返回")
open class CreateOrderResponseResult {
  @Schema(title = "32位随机字符串")
  open var nonceStr: String? = null

  @Schema(name = "package")
  @JsonProperty(value = "package")
  open var packageStr: String? = null

  @Schema(title = "时间戳秒")
  open var timeStamp: Long? = null

  @Schema(title = "加密方法，SHA256-RSA")
  open var signType: String? = null

  @Schema(title = "加密字符串")
  open var paySign: String? = null
}
