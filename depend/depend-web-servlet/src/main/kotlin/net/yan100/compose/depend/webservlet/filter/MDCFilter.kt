/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.depend.webservlet.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import net.yan100.compose.depend.webservlet.remoteRequestIp
import org.slf4j.MDC
import java.util.*

/** # 抽象 MDC Filter */
abstract class MDCFilter : Filter {
  /**
   * ## 提供 跟踪 id
   *
   * @return 跟踪id
   */
  open fun supportTranceId(): String? {
    return UUID.randomUUID().toString()
  }

  /**
   * ## ip
   *
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
