package net.yan100.compose.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.yan100.compose.domain.AuthRequestInfo
import net.yan100.compose.security.spring.security.SecurityPreflightValidFilter
import net.yan100.compose.slf4j
import net.yan100.compose.util.IEmptyDefault

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
