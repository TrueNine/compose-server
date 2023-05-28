package net.yan100.compose.pay.typing

import net.yan100.compose.core.lang.StringTyping

enum class WechatPayGrantTyping(
  private val typingCode: String
) : StringTyping {
  AUTH_CODE("authorization_code");

  override fun getValue(): String? {
    return this.typingCode
  }
}
