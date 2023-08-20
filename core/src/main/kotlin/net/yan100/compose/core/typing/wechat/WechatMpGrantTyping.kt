package net.yan100.compose.core.typing.wechat

import com.fasterxml.jackson.annotation.JsonValue
import net.yan100.compose.core.lang.StringTyping

/**
 * # 微信支付验证类型
 * @author TrueNine
 * @since 2023-05-31
 */
enum class WechatMpGrantTyping(
  private val typingCode: String
) : StringTyping {
  AUTH_CODE("authorization_code");

  @JsonValue
  override fun getValue(): String? {
    return this.typingCode
  }

  companion object {
    @JvmStatic
    fun findVal(v: String?) = entries.find { it.typingCode == v }
  }
}
