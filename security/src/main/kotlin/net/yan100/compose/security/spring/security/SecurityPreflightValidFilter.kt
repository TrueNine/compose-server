package net.yan100.compose.security.spring.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.yan100.compose.core.ctx.UserInfoContextHolder
import net.yan100.compose.core.http.Headers
import net.yan100.compose.core.http.Methods
import net.yan100.compose.core.lang.deviceId
import net.yan100.compose.core.lang.hasText
import net.yan100.compose.core.lang.remoteRequestIp
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.core.models.AuthRequestInfo
import net.yan100.compose.security.UserDetailsWrapper
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
abstract class SecurityPreflightValidFilter : OncePerRequestFilter() {

  private val log = slf4j(this::class)

  @Throws(ServletException::class, IOException::class)
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    // 跨域请求直接放行
    if (request.method == Methods.OPTIONS) {
      log.info("直接放行预检请求 uri = {}", request.requestURI)
      filterChain.doFilter(request, response)
      return
    }
    val authInfo = if (containsTokenPair(request)) {
      val token = getToken(request)
      val ref = getRefreshToken(request)
      getUserAuthorizationInfo(token, ref, request, response)
    } else {
      log.trace("没有发现用户信息，直接放行")
      filterChain.doFilter(request, response)
      return
    }
    if (null == authInfo) {
      log.trace("用户信息错误，直接放行")
      filterChain.doFilter(request, response)
      return
    }

    authInfo.currentIpAddr = request.remoteRequestIp
    authInfo.deviceId = request.deviceId

    log.trace("获取到用户信息 = {}", authInfo)
    val details = UserDetailsWrapper(authInfo)

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
    log.info("set user = {}", UserInfoContextHolder.get())
    log.trace("过滤器放行")
    filterChain.doFilter(request, response)
  }

  /**
   * 校验其是否包含验证令牌
   * @param request 请求
   * @return [Boolean]
   */
  private fun containsTokenPair(request: HttpServletRequest): Boolean = request.getHeader(Headers.AUTHORIZATION).hasText() && request.getHeader(Headers.X_REFRESH).hasText()


  /**
   * 从请求得到 token
   *
   * @param request 请求
   * @return [String]
   */
  private fun getToken(request: HttpServletRequest?): String? = request?.getHeader(Headers.AUTHORIZATION)


  /**
   * 从请求获得 re-flash 令牌
   *
   * @param request 请求
   * @return [String]
   */
  private fun getRefreshToken(request: HttpServletRequest?): String? = request?.getHeader(Headers.X_REFRESH)

  /**
   * 合法性检查
   *
   * @param token token
   * @param reFlashToken re-flash
   * @param request  请求
   * @param response 响应
   * @return [AuthRequestInfo]
   */
  protected abstract fun getUserAuthorizationInfo(
    token: String?,
    reFlashToken: String?,
    request: HttpServletRequest,
    response: HttpServletResponse
  ): AuthRequestInfo?
}
