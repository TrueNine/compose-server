package net.yan100.compose.pay.models.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema


@Schema(title = "拉起小程序订单返回")
open class PullUpMpPayOrderResponseResult {

  @Schema(title = "32位随机字符串")
  open var nonceStr: String? = null

  @Schema(name = "package")
  @JsonProperty(value = "package")
  open var packageStr: String? = null

  @Schema(title = "时间戳 秒")
  open var timeStamp: Long? = null

  @Schema(title = "签名方法，SHA256-RSA")
  open var signType: String? = null

  @Schema(title = "签名字符串")
  open var paySign: String? = null
}
