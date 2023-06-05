package net.yan100.compose.core.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

/**
 * # 支付渠道类型枚举
 * @author TrueNine
 * @since 2023-05-04
 */
@Schema(title = "支付渠道类型")
enum class PayChannelTyping(
  private val channelId: Int
) : IntTyping {
  @Schema(title = "微信支付")
  WECHAT(0),
  @Schema(title = "支付宝")
  ALIPAY(1);

  @JsonValue
  override fun getValue(): Int = channelId

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = PayChannelTyping.values().find { it.channelId == v }
  }
}
