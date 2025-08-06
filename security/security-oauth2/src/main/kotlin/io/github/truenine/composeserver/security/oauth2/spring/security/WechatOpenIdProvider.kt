package io.github.truenine.composeserver.security.oauth2.spring.security

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

class WechatOpenIdProvider : AuthenticationProvider {
  override fun authenticate(authentication: Authentication?): Authentication {
    TODO("Not yet implemented")
  }

  override fun supports(authentication: Class<*>?): Boolean {
    TODO("Not yet implemented")
  }
}
