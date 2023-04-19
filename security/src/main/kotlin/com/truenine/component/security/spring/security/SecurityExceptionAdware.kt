package com.truenine.component.security.spring.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.http.ErrMsg
import com.truenine.component.core.http.ErrorMessage
import com.truenine.component.core.http.MediaTypes
import com.truenine.component.core.lang.LogKt
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import java.util.*

/**
 * 异常过滤器
 *
 * @author TrueNine
 * @since 2022-09-28
 */
abstract class SecurityExceptionAdware(
  private var mapper: ObjectMapper? = null
) : AccessDeniedHandler, AuthenticationEntryPoint {
  override fun commence(
    request: HttpServletRequest,
    response: HttpServletResponse,
    ex: AuthenticationException
  ) {
    log.warn("授权校验异常", ex)
    writeErrorMessage(response, ErrorMessage.failedByMessages(ErrMsg._401))
  }

  override fun handle(
    request: HttpServletRequest,
    response: HttpServletResponse,
    ex: AccessDeniedException
  ) {
    log.warn("无权限异常", ex)
    writeErrorMessage(response, ErrorMessage.failedByMessages(ErrMsg._403))
  }

  private fun writeErrorMessage(response: HttpServletResponse, msg: ErrorMessage) {
    response.status = msg.code
    response.characterEncoding = "UTF-8"
    response.contentType = MediaTypes.JSON.media()
    response.locale = Locale("zh-CN", "CN")
    response.writer.print(mapper?.writeValueAsString(msg))
  }

  companion object {
    @JvmStatic
    private val log = LogKt.getLog(this::class)
  }
}
