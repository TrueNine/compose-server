package io.github.truenine.composeserver.security.xss.antisamy

import io.github.truenine.composeserver.slf4j
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.owasp.validator.html.AntiSamy
import org.owasp.validator.html.Policy
import org.slf4j.Logger

/**
 * XSS request filter.
 *
 * Antisamy wrapper.
 *
 * @param request Wrapped HttpServletRequest
 * @author TrueNine
 * @since 2023-04-20
 */
// TODO add this wrapper
class AntisamyRequestWrapper(request: HttpServletRequest?) : HttpServletRequestWrapper(request) {

  override fun getParameterValues(name: String?): Array<String?>? {
    val params = super.getParameterValues(name) ?: return null
    log.trace("antisamy filtering params = {} >-> {}", name, params)
    return params.mapNotNull { filterParams(it) }.toTypedArray()
  }

  private fun filterParams(param: String?): String? {
    return ANTI_SAMY.scan(param, POLICY).cleanHTML
  }

  companion object {
    @JvmStatic private val POLICY: Policy = Policy.getInstance("antisamy-ebay.xml")

    @JvmStatic private val ANTI_SAMY = AntiSamy()

    @JvmStatic private val log: Logger = slf4j(this::class)
  }
}
