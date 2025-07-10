package net.yan100.compose.pay.models

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * ## 微信支付成功通知
 *
 * [支付通知api](https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_5.shtml)
 *
 * @author TrueNine
 * @since 2023-05-06
 */
@Deprecated(message = "暂时不用封装")
@Schema(title = "微信支付成功通知回调")
data class WechatPaySuccessVo(
  @Schema(title = "通知 id") var id: String? = null,
  @Schema(title = "创建时间") @JsonProperty("create_time") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") var createDatetime: LocalDateTime? = null,
  @Schema(title = "通知类型", description = "通知的类型，支付成功通知的类型为TRANSACTION.SUCCESS") @JsonProperty("event_type") var eventType: String? = null,
  @Schema(title = "通知数据类型") @JsonProperty("resource_type") var resourceType: String? = null,
  @Schema(title = "通知数据") @JsonProperty("resource") var resource: WechatPaySuccessVoResource? = null,
)
