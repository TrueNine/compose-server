package com.truenine.component.security.xss.antisamy

import com.truenine.component.core.lang.LogKt
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.owasp.validator.html.AntiSamy
import org.owasp.validator.html.Policy
import org.slf4j.Logger

/**
 * xss 请求过滤器
 *
 * antisamy 包装器
 * @author TrueNine
 * @param request 被包装请求
 */
class AntisamyRequestWrapper(request: HttpServletRequest?) :
  HttpServletRequestWrapper(request) {

  override fun getParameterValues(name: String?): Array<String?>? {
    val params = super.getParameterValues(name) ?: return null
    log.debug("antisamy 过滤参数 = {} === {}", name, params)
    return params.mapNotNull { filterParams(it) }.toTypedArray()
  }

  private fun filterParams(param: String?): String? {
    return ANTI_SAMY.scan(param, POLICY).cleanHTML
  }

  companion object {
    @JvmStatic
    private val POLICY: Policy = Policy.getInstance("antisamy-ebay.xml")

    @JvmStatic
    private val ANTI_SAMY = AntiSamy()

    @JvmStatic
    private val log: Logger = LogKt.getLog(AntisamyRequestWrapper::class)
  }
}