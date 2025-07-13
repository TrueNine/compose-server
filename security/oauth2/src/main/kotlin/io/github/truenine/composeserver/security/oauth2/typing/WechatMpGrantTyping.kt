package io.github.truenine.composeserver.security.oauth2.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.github.truenine.composeserver.IStringTyping

/**
 * # 微信支付验证类型
 *
 * @author TrueNine
 * @since 2023-05-31
 */
enum class WechatMpGrantTyping(private val typingCode: String) : IStringTyping {
  CLIENT_CREDENTIAL("client_credential"),
  AUTH_CODE("authorization_code");

  @JsonValue override val value: String = typingCode

  companion object {
    @JvmStatic fun findVal(v: String?) = entries.find { it.typingCode == v }
  }
}
