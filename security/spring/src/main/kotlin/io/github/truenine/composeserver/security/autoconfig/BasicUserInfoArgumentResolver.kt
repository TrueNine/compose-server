package io.github.truenine.composeserver.security.autoconfig

import io.github.truenine.composeserver.consts.IHeaders
import io.github.truenine.composeserver.depend.servlet.remoteRequestIp
import io.github.truenine.composeserver.domain.RequestInfo
import io.github.truenine.composeserver.security.holders.UserInfoContextHolder
import io.github.truenine.composeserver.slf4j
import jakarta.servlet.http.HttpServletRequest
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
    log.trace("argument injection for: {}", u)
    if (u == null) {
      val req = webRequest.nativeRequest as HttpServletRequest
      val deviceId = IHeaders.getDeviceId(req)
      UserInfoContextHolder.set(RequestInfo(currentIpAddr = req.remoteRequestIp, deviceId = deviceId))
    }
    return u
  }
}
