package io.github.truenine.composeserver.depend.servlet.filter

import io.github.truenine.composeserver.depend.servlet.remoteRequestIp
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC
import java.util.*

/** # Abstract MDC Filter */
abstract class MDCFilter : Filter {
  /**
   * ## Provides a trace ID
   *
   * @return trace ID
   */
  open fun supportTranceId(): String? {
    return UUID.randomUUID().toString()
  }

  /**
   * ## IP address
   *
   * @return IP address
   */
  open fun supportIp(req: HttpServletRequest): String? {
    return req.remoteRequestIp
  }

  override fun doFilter(req: ServletRequest?, resp: ServletResponse?, c: FilterChain?) {
    try {
      req?.also { q ->
        if (q is HttpServletRequest) {
          var tid: String? = q.getHeader("tid")
          if (tid == null) {
            tid = supportTranceId() ?: ""
          }
          MDC.put("tid", tid)
          MDC.put("ip", supportIp(q))
        }
      }
    } finally {
      c?.doFilter(req, resp)
      MDC.clear()
    }
  }
}
