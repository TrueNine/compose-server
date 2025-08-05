package io.github.truenine.composeserver.pay

import com.fasterxml.jackson.annotation.JsonValue
<<<<<<<< HEAD:pay/shared/src/main/kotlin/io/github/truenine/composeserver/pay/PayChannelTyping.kt
import io.github.truenine.composeserver.IIntTyping
========
import io.github.truenine.composeserver.IIntEnum
>>>>>>>> dev:pay/shared/src/main/kotlin/io/github/truenine/composeserver/pay/PayChannel.kt
import io.swagger.v3.oas.annotations.media.Schema

/**
 * # 支付渠道类型枚举
 *
 * @author TrueNine
 * @since 2023-05-04
 */
@Schema(title = "支付渠道类型")
<<<<<<<< HEAD:pay/shared/src/main/kotlin/io/github/truenine/composeserver/pay/PayChannelTyping.kt
enum class PayChannelTyping(private val channelId: Int) : IIntTyping {
========
enum class PayChannel(private val channelId: Int) : IIntEnum {
>>>>>>>> dev:pay/shared/src/main/kotlin/io/github/truenine/composeserver/pay/PayChannel.kt
  @Schema(title = "微信支付") WECHAT(0),
  @Schema(title = "支付宝") ALIPAY(1);

  @JsonValue override val value: Int = channelId

  companion object {
    @JvmStatic fun findVal(v: Int?) = entries.find { it.channelId == v }
  }
}
