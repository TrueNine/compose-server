package io.github.truenine.composeserver.pay.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.github.truenine.composeserver.IIntTyping
import io.swagger.v3.oas.annotations.media.Schema

/**
 * # 支付渠道类型枚举
 *
 * @author TrueNine
 * @since 2023-05-04
 */
@Schema(title = "支付渠道类型")
enum class PayChannelTyping(private val channelId: Int) : IIntTyping {
  @Schema(title = "微信支付") WECHAT(0),
  @Schema(title = "支付宝") ALIPAY(1);

  @JsonValue override val value: Int = channelId

  companion object {
    @JvmStatic fun findVal(v: Int?) = entries.find { it.channelId == v }
  }
}
