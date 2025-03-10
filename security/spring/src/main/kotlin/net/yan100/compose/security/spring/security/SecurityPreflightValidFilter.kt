package net.yan100.compose.security.spring.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.IOException
import net.yan100.compose.core.consts.IHeaders
import net.yan100.compose.core.consts.IMethods
import net.yan100.compose.core.domain.AuthRequestInfo
import net.yan100.compose.core.hasText
import net.yan100.compose.core.slf4j
import net.yan100.compose.depend.servlet.deviceId
import net.yan100.compose.depend.servlet.remoteRequestIp
import net.yan100.compose.security.UserDetailsWrapper
import net.yan100.compose.security.holders.UserInfoContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

private val log = slf4j<SecurityPreflightValidFilter>()

/**
 * jwt过滤器
 *
 * @author TrueNine
 * @since 2022-10-28
 */
abstract class SecurityPreflightValidFilter : OncePerRequestFilter() {

  @Throws(ServletException::class, IOException::class)
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain,
  ) {
    // 跨域请求直接放行
    if (request.method == IMethods.OPTIONS) {
      log.trace("直接放行预检请求 uri = {}", request.requestURI)
      filterChain.doFilter(request, response)
      return
    }
    val authInfo =
      if (containsTokenPair(request)) {
        val token = getToken(request)
        val ref = getRefreshToken(request)
        getUserAuthorizationInfo(token, ref, request, response)
          ?.copy(
            currentIpAddr = request.remoteRequestIp,
            deviceId = request.deviceId,
          )
      } else {
        log.trace("没有发现用户信息，直接放行")
        filterChain.doFilter(request, response)
        return
      }

    if (null == authInfo) {
      log.trace("用户信息 = null，直接放行")
      filterChain.doFilter(request, response)
      return
    }

    log.trace("获取到用户信息 = {}", authInfo)
    val details = UserDetailsWrapper(authInfo)

    log.trace("获取到 details = {}", details)

    val usernamePasswordAuthenticationToken =
      UsernamePasswordAuthenticationToken(
        details,
        details.password,
        details.authorities,
      )
    log.trace("upa = {}", usernamePasswordAuthenticationToken)
    // 设置验证信息过滤器放行
    SecurityContextHolder.getContext().authentication =
      usernamePasswordAuthenticationToken
    // 向用户信息内设置信息
    UserInfoContextHolder.set(authInfo)
    log.trace("set user = {}", UserInfoContextHolder.get())
    filterChain.doFilter(request, response)
  }

  /**
   * 校验其是否包含验证令牌
   *
   * @param request 请求
   * @return [Boolean]
   */
  private fun containsTokenPair(request: HttpServletRequest): Boolean =
    request.getHeader(IHeaders.AUTHORIZATION).hasText() &&
      request.getHeader(IHeaders.X_REFRESH).hasText()

  /**
   * 从请求得到 token
   *
   * @param request 请求
   * @return [String]
   */
  private fun getToken(request: HttpServletRequest?): String? =
    request?.getHeader(IHeaders.AUTHORIZATION)

  /**
   * 从请求获得 re-flash 令牌
   *
   * @param request 请求
   * @return [String]
   */
  private fun getRefreshToken(request: HttpServletRequest?): String? =
    request?.getHeader(IHeaders.X_REFRESH)

  /**
   * 合法性检查
   *
   * @param token token
   * @param reFlashToken re-flash
   * @param request 请求
   * @param response 响应
   * @return [AuthRequestInfo]
   */
  protected abstract fun getUserAuthorizationInfo(
    token: String?,
    reFlashToken: String?,
    request: HttpServletRequest,
    response: HttpServletResponse,
  ): AuthRequestInfo?
}
