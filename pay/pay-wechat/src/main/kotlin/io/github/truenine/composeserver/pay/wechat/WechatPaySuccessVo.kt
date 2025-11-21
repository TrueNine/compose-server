package io.github.truenine.composeserver.pay.wechat

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * ## WeChat payment success notification
 *
 * [Payment notification API](https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_5.shtml)
 *
 * @author TrueNine
 * @since 2023-05-06
 */
@Deprecated(message = "Temporarily not used")
@Schema(title = "WeChat payment success notification callback")
data class WechatPaySuccessVo(
  @Schema(title = "Notification ID") var id: String? = null,
  @Schema(title = "Creation time") @JsonProperty("create_time") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") var createDatetime: LocalDateTime? = null,
  @Schema(title = "Notification type", description = "The type of notification; for payment success it is TRANSACTION.SUCCESS") @JsonProperty("event_type") var eventType: String? = null,
  @Schema(title = "Notification data type") @JsonProperty("resource_type") var resourceType: String? = null,
  @Schema(title = "Notification data") @JsonProperty("resource") var resource: WechatPaySuccessVoResource? = null,
)
