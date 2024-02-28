/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.security.xss.antisamy

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import net.yan100.compose.core.log.slf4j
import org.owasp.validator.html.AntiSamy
import org.owasp.validator.html.Policy
import org.slf4j.Logger

/**
 * xss 请求过滤器
 *
 * antisamy 包装器
 *
 * @param request 被包装请求
 * @author TrueNine
 * @since 2023-04-20
 */
// TODO 加入此类
class AntisamyRequestWrapper(request: HttpServletRequest?) : HttpServletRequestWrapper(request) {

  override fun getParameterValues(name: String?): Array<String?>? {
    val params = super.getParameterValues(name) ?: return null
    log.trace("antisamy 过滤参数 = {} >-> {}", name, params)
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
