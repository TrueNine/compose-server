package net.yan100.compose.pay.models.resp

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull


@Schema(title = "拉起小程序支付微信返回")
class CreateMpPayOrderResp {
  @Schema(title = "32位随机字符串，32位以下")
  @Min(value = 5, message = "随机字符串太短")
  @Max(value = 32, message = "随机字符串不得超过32位")
  @JsonProperty("nonceStr")
  var random32String: String? = null

  @JsonProperty(value = "package")
  @Schema(
    title = "统一下单接口返回的 prepay_id", description = """
    prepay_id 参数值，提交格式如：prepay_id=***
  """
  )
  var prePayId: String? = null
    get() = field?.replace("prepay_id=", "")
    set(f) {
      field = "prepay_id=$f"
    }

  @Min(value = 0)
  @JsonProperty("timeStamp")
  @Schema(title = "时间戳 秒")
  var isIso8601Second: String? = null

  @Schema(title = "签名方法，SHA256-RSA")
  var signType: String? = "RSA"

  @NotNull
  @Schema(title = "签名字符串")
  var paySign: String? = null
}
