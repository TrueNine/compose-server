package net.yan100.compose.pay.typing

import com.fasterxml.jackson.annotation.JsonValue
import net.yan100.compose.core.lang.StringTyping

enum class WechatPayGrantTyping(
  private val typingCode: String
) : StringTyping {
  AUTH_CODE("authorization_code");

  @JsonValue
  override fun getValue(): String? {
    return this.typingCode
  }

  companion object {
    @JvmStatic
    fun findVal(v: String?) = WechatPayGrantTyping.values().find { it.typingCode == v }
  }
}
