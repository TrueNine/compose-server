package net.yan100.compose.security.spring.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.nio.charset.Charset
import java.util.*
import net.yan100.compose.core.ErrorBody
import net.yan100.compose.core.slf4j
import net.yan100.compose.core.typing.HttpStatusTyping
import net.yan100.compose.core.typing.MimeTypes
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
abstract class SecurityExceptionAdware(
  private var mapper: ObjectMapper? = null
) : AccessDeniedHandler, AuthenticationEntryPoint {
  override fun commence(
    request: HttpServletRequest,
    response: HttpServletResponse,
    ex: AuthenticationException,
  ) {
    log.warn("授权校验异常", ex)
    writeErrorMessage(
      response,
      ErrorBody.failedByHttpStatus(HttpStatusTyping._401),
    )
  }

  override fun handle(
    request: HttpServletRequest,
    response: HttpServletResponse,
    ex: AccessDeniedException,
  ) {
    log.warn("无权限异常", ex)
    writeErrorMessage(
      response,
      ErrorBody.failedByHttpStatus(HttpStatusTyping._403),
    )
  }

  private fun writeErrorMessage(
    response: HttpServletResponse,
    msg: ErrorBody,
    charset: Charset = Charsets.UTF_8,
  ) {
    response.status = msg.code!!
    response.characterEncoding = charset.displayName()
    response.contentType = MimeTypes.JSON.value
    response.locale = Locale.CHINA
    val write = response.writer
    write.print(mapper?.writeValueAsString(msg))
    write.flush()
  }

  companion object {
    @JvmStatic private val log = slf4j(SecurityExceptionAdware::class)
  }
}
