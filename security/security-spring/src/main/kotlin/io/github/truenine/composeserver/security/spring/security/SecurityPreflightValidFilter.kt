package io.github.truenine.composeserver.security.spring.security

import io.github.truenine.composeserver.consts.IHeaders
import io.github.truenine.composeserver.consts.IMethods
import io.github.truenine.composeserver.depend.servlet.deviceId
import io.github.truenine.composeserver.depend.servlet.remoteRequestIp
import io.github.truenine.composeserver.domain.AuthRequestInfo
import io.github.truenine.composeserver.hasText
import io.github.truenine.composeserver.security.UserDetailsWrapper
import io.github.truenine.composeserver.security.holders.UserInfoContextHolder
import io.github.truenine.composeserver.slf4j
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.IOException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

private val log = slf4j<SecurityPreflightValidFilter>()

/**
 * JWT preflight validation filter.
 *
 * @author TrueNine
 * @since 2022-10-28
 */
abstract class SecurityPreflightValidFilter : OncePerRequestFilter() {

  @Throws(ServletException::class, IOException::class)
  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    // Allow CORS preflight requests to pass through directly
    if (request.method == IMethods.OPTIONS) {
      log.trace("Allowing preflight request, uri = {}", request.requestURI)
      filterChain.doFilter(request, response)
      return
    }
    val authInfo =
      if (containsTokenPair(request)) {
        val token = getToken(request)
        val ref = getRefreshToken(request)
        getUserAuthorizationInfo(token, ref, request, response)?.copy(currentIpAddr = request.remoteRequestIp, deviceId = request.deviceId)
      } else {
        log.trace("No user information found, passing through directly")
        filterChain.doFilter(request, response)
        return
      }

    if (null == authInfo) {
      log.trace("User information is null, passing through directly")
      filterChain.doFilter(request, response)
      return
    }

    log.trace("Obtained user information = {}", authInfo)
    val details = UserDetailsWrapper(authInfo)

    log.trace("Obtained details = {}", details)

    val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(details, details.password, details.authorities)
    log.trace("upa = {}", usernamePasswordAuthenticationToken)
    // Set authentication in security context and continue filter chain
    SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
    // Set user information into UserInfoContextHolder
    UserInfoContextHolder.set(authInfo)
    log.trace("Set user = {}", UserInfoContextHolder.get())
    filterChain.doFilter(request, response)
  }

  /**
   * Check whether the request contains both access token and refresh token headers.
   *
   * @param request HTTP request
   * @return [Boolean]
   */
  private fun containsTokenPair(request: HttpServletRequest): Boolean =
    request.getHeader(IHeaders.AUTHORIZATION).hasText() && request.getHeader(IHeaders.X_REFRESH).hasText()

  /**
   * Get access token from the request.
   *
   * @param request HTTP request
   * @return [String]
   */
  private fun getToken(request: HttpServletRequest?): String? = request?.getHeader(IHeaders.AUTHORIZATION)

  /**
   * Get refresh token from the request.
   *
   * @param request HTTP request
   * @return [String]
   */
  private fun getRefreshToken(request: HttpServletRequest?): String? = request?.getHeader(IHeaders.X_REFRESH)

  /**
   * Validate token pair and return authorization information.
   *
   * @param token Access token
   * @param reFlashToken Refresh token
   * @param request HTTP request
   * @param response HTTP response
   * @return [AuthRequestInfo]
   */
  protected abstract fun getUserAuthorizationInfo(
    token: String?,
    reFlashToken: String?,
    request: HttpServletRequest,
    response: HttpServletResponse,
  ): AuthRequestInfo?
}
