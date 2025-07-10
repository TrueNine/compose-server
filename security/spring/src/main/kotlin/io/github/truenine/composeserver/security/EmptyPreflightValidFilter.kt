package io.github.truenine.composeserver.security

import io.github.truenine.composeserver.domain.AuthRequestInfo
import io.github.truenine.composeserver.security.spring.security.SecurityPreflightValidFilter
import io.github.truenine.composeserver.slf4j
import io.github.truenine.composeserver.util.IEmptyDefault
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class EmptyPreflightValidFilter : IEmptyDefault, SecurityPreflightValidFilter() {
  companion object {
    @JvmStatic private val log = slf4j(EmptyPreflightValidFilter::class)
  }

  init {
    log.warn("正在使用默认的jwt过滤器")
  }

  override fun getUserAuthorizationInfo(token: String?, reFlashToken: String?, request: HttpServletRequest, response: HttpServletResponse): AuthRequestInfo? {
    return null
  }
}
