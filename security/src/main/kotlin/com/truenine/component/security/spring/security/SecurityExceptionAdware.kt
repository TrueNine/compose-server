package com.truenine.component.security.spring.security

import com.truenine.component.core.api.http.R
import com.truenine.component.core.api.http.Status
import com.truenine.component.core.lang.LogKt
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler

/**
 * 异常过滤器
 *
 * @author TrueNine
 * @since 2022-09-28
 */
abstract class SecurityExceptionAdware : AccessDeniedHandler,
  AuthenticationEntryPoint {
  override fun commence(
    request: HttpServletRequest,
    response: HttpServletResponse,
    ex: AuthenticationException
  ) {
    log.debug("授权异常", ex)
    R.failed(ex, Status._401).writeJson(response)
  }

  override fun handle(
    request: HttpServletRequest,
    response: HttpServletResponse,
    ex: AccessDeniedException
  ) {
    log.debug("无权限异常", ex)
    R.failed(ex, 403).writeJson(response)
  }

  companion object {
    @JvmStatic
    private val log = LogKt.getLog(SecurityExceptionAdware::class)
  }
}
