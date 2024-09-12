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
package net.yan100.compose.depend.webservlet.autoconfig

import jakarta.servlet.http.HttpServletRequest
import net.yan100.compose.core.consts.IHeaders
import net.yan100.compose.core.consts.IInterAddr
import net.yan100.compose.core.encrypt.RequestInfo
import net.yan100.compose.core.holders.UserInfoContextHolder
import net.yan100.compose.core.slf4j
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

private val log = slf4j(BasicUserInfoArgumentResolver::class)

@Component
class BasicUserInfoArgumentResolver : HandlerMethodArgumentResolver, WebMvcConfigurer {

  override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
    super.addArgumentResolvers(resolvers)
    log.trace("注册用户信息拦ArgumentResolver")
    resolvers.add(this)
  }

  override fun supportsParameter(parameter: MethodParameter): Boolean {
    log.trace("support by parameter = {}", parameter)
    return RequestInfo::class.java.isAssignableFrom(parameter.parameterType)
  }

  override fun resolveArgument(
    parameter: MethodParameter,
    mavContainer: ModelAndViewContainer?,
    webRequest: NativeWebRequest,
    binderFactory: WebDataBinderFactory?,
  ): Any? {
    val u = UserInfoContextHolder.get()
    log.trace("argument injection for {}", u)
    if (u == null) {
      UserInfoContextHolder.set(
        RequestInfo().apply {
          val req = webRequest.nativeRequest as HttpServletRequest
          val deviceId = IHeaders.getDeviceId(req)
          this.currentIpAddr = IInterAddr.getRequestIpAddress(req)
          this.deviceId = deviceId
        }
      )
    }
    return u
  }
}
