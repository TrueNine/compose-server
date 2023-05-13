package net.yan100.compose.core.autoconfig

import net.yan100.compose.core.ctx.UserInfoContextHolder
import net.yan100.compose.core.lang.slf4j
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer


@Configuration
class BasicUserInfoArgumentResolver : HandlerMethodArgumentResolver {
  private val log = slf4j(BasicUserInfoArgumentResolver::class)
  override fun supportsParameter(parameter: MethodParameter): Boolean {
    return parameter.parameter.type == net.yan100.compose.core.models.BasicUserInfoModel::class.java
  }

  override fun resolveArgument(
    parameter: MethodParameter,
    mavContainer: ModelAndViewContainer?,
    webRequest: NativeWebRequest,
    binderFactory: WebDataBinderFactory?
  ): Any? {
    val u = UserInfoContextHolder.get()
    log.trace("当前用户 = {}", u)
    return u
  }
}
