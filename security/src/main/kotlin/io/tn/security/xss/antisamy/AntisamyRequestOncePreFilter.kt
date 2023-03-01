package io.tn.security.xss.antisamy

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.web.filter.OncePerRequestFilter


class AntisamyRequestOncePreFilter : OncePerRequestFilter() {
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) = filterChain.doFilter(AntisamyRequestWrapper(request), response)

}
