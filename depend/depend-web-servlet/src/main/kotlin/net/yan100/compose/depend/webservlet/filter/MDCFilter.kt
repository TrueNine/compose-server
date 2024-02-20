package net.yan100.compose.depend.webservlet.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import net.yan100.compose.core.lang.remoteRequestIp
import org.slf4j.MDC
import java.util.*

/**
 * ## 抽象 MDC Filter
 *
 */
abstract class MDCFilter : Filter {
    /**
     * ## 提供 跟踪 id
     * @return 跟踪id
     */
    open fun supportTranceId(): String? {
        return UUID.randomUUID().toString()
    }

    /**
     * ## ip
     * @return ip
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
