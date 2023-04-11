package com.truenine.component.security.spring.security

import com.truenine.component.core.ctx.UserInfoContextHolder
import com.truenine.component.core.http.Headers
import com.truenine.component.core.lang.LogKt
import com.truenine.component.core.lang.Str
import com.truenine.component.core.models.UserAuthorizationInfoModel
import com.truenine.component.security.SecurityUserDetails
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

/**
 * jwt过滤器
 *
 * @author TrueNine
 * @since 2022-10-28
 */
abstract class SecurityPreValidFilter : OncePerRequestFilter() {

  private val log = LogKt.getLog(this::class)

  @Throws(ServletException::class, IOException::class)
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    val authInfo = if (containsTokenPair(request)) {
      val token = getTokenMapping(request)
      val ref = getReFlashTokenMapping(request)
      getUserAuthorizationInfo(token, ref, request, response)
    } else {
      log.trace("没有发现用户信息，直接放行")
      filterChain.doFilter(request, response)
      return
    }
    log.trace("获取到用户信息 = {}", authInfo)

    val details =
      SecurityUserDetails(
        authInfo
      )
    log.trace("获取到 details = {}", details)

    val usernamePasswordAuthenticationToken =
      UsernamePasswordAuthenticationToken(
        details,
        details.password,
        details.authorities
      )
    log.trace("upa = {}", usernamePasswordAuthenticationToken)
    // 设置验证信息过滤器放行
    SecurityContextHolder.getContext().authentication =
      usernamePasswordAuthenticationToken
    // 向用户信息内设置信息
    UserInfoContextHolder.set(authInfo)
    log.trace("过滤器放行")
    filterChain.doFilter(request, response)
  }

  private fun containsTokenPair(request: HttpServletRequest): Boolean =
    Str.hasText(request.getHeader(Headers.AUTHORIZATION))
      && Str.hasText(request.getHeader(Headers.X_RE_FLUSH_TOKEN))


  /**
   * 从请求得到 token
   *
   * @param request 请求
   * @return [String]
   */
  private fun getTokenMapping(request: HttpServletRequest?): String? =
    request?.getHeader(Headers.AUTHORIZATION)


  /**
   * 从请求获得 re-flash 令牌
   *
   * @param request 请求
   * @return [String]
   */
  private fun getReFlashTokenMapping(request: HttpServletRequest?): String? =
    request?.getHeader(Headers.X_RE_FLUSH_TOKEN)

  /**
   * 合法性检查
   *
   * @param token token
   * @param reFlash re-flash
   * @param request  请求
   * @param response 响应
   * @return [UserAuthorizationInfoModel]
   */
  protected abstract fun getUserAuthorizationInfo(
    token: String?,
    reFlash: String?,
    request: HttpServletRequest,
    response: HttpServletResponse
  ): UserAuthorizationInfoModel
}
