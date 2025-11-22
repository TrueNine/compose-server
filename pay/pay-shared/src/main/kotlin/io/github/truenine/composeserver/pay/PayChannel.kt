package io.github.truenine.composeserver.pay

import com.fasterxml.jackson.annotation.JsonValue
import io.github.truenine.composeserver.IIntEnum
import io.swagger.v3.oas.annotations.media.Schema

/**
 * # Payment channel type enum
 *
 * @author TrueNine
 * @since 2023-05-04
 */
@Schema(title = "Payment channel type")
enum class PayChannel(private val channelId: Int) : IIntEnum {
  @Schema(title = "WeChat Pay") WECHAT(0),
  @Schema(title = "Alipay") ALIPAY(1);

  @JsonValue override val value: Int = channelId

  companion object {
    @JvmStatic fun findVal(v: Int?) = entries.find { it.channelId == v }
  }
}
