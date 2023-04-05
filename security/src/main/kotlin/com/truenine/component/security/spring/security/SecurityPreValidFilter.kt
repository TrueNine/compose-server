package com.truenine.component.security.spring.security

import com.truenine.component.core.ctx.UserInfoContextHolder
import com.truenine.component.core.http.Headers
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
  @Throws(ServletException::class, IOException::class)
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {

    val authInfo = if (containsTokenPair(request)) {
      val token = getTokenMapping(request)
      val ref = getReFlashTokenMapping(request)
      converterSecurityUserInfo(token, ref, request, response)
    } else {
      filterChain.doFilter(request, response)
      return
    }

    val details =
      SecurityUserDetails(
        authInfo
      )

    val usernamePasswordAuthenticationToken =
      UsernamePasswordAuthenticationToken(
        details,
        details.password,
        details.authorities
      )

    // 设置验证信息过滤器放行
    SecurityContextHolder.getContext().authentication =
      usernamePasswordAuthenticationToken
    UserInfoContextHolder.set(authInfo)
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
   * jwt合法性检查
   *
   * @param token token
   * @param reFlash re-flash
   * @param request  请求
   * @param response 响应
   * @return [UserAuthorizationInfoModel]
   */
  protected abstract fun converterSecurityUserInfo(
    token: String?,
    reFlash: String?,
    request: HttpServletRequest,
    response: HttpServletResponse
  ): UserAuthorizationInfoModel
}
