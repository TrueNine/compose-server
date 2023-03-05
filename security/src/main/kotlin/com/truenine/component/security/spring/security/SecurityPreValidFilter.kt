package com.truenine.component.security.spring.security

import com.truenine.component.core.api.http.Headers
import com.truenine.component.core.api.http.ParameterNames
import com.truenine.component.core.db.Bf
import com.truenine.component.core.lang.Str
import com.truenine.component.security.spring.security.wrappers.SecurityUserDetails
import com.truenine.component.security.spring.security.wrappers.SecurityUserInfo
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
    // 从请求参数拿取用户租户 id
    val tenantRequestId =
      request.getParameter(ParameterNames.X_TENANT_ID)
        ?: Bf.Tenant.DEFAULT_TENANT

    val securityUserInfo = if (containsTokenPair(request)) {
      val token = getTokenMapping(request)
      val ref = getReFlashTokenMapping(request)
      converterSecurityUserInfo(token, ref, request, response)
    } else {
      request.setAttribute(ParameterNames.X_TENANT_ID, tenantRequestId)
      filterChain.doFilter(request, response)
      return
    }
    // 传出 tenant id 到请求参数
    request.setAttribute(Headers.X_INTERNAL_TENANT_ID, securityUserInfo.tenant)

    val details = SecurityUserDetails(
      securityUserInfo
    )

    val usernamePasswordAuthenticationToken =
      UsernamePasswordAuthenticationToken(
        details,
        details.password,
        details.authorities
      )

    // 过滤器放行
    SecurityContextHolder.getContext().authentication =
      usernamePasswordAuthenticationToken
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
   * @return [SecurityUserInfo]
   */
  protected abstract fun converterSecurityUserInfo(
    token: String?,
    reFlash: String?,
    request: HttpServletRequest,
    response: HttpServletResponse
  ): SecurityUserInfo
}
