package io.github.truenine.composeserver.security.oauth2.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.github.truenine.composeserver.IStringEnum

/**
 * WeChat Mini Program OAuth2 grant types.
 *
 * @author TrueNine
 * @since 2023-05-31
 */
enum class WechatMpGrantTyping(private val typingCode: String) : IStringEnum {
  CLIENT_CREDENTIAL("client_credential"),
  AUTH_CODE("authorization_code");

  @JsonValue override val value: String = typingCode

  companion object {
    @JvmStatic fun findVal(v: String?) = entries.find { it.typingCode == v }
  }
}
