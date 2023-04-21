package com.truenine.component.core.autoconfig

import com.truenine.component.core.ctx.UserInfoContextHolder
import com.truenine.component.core.lang.LogKt
import com.truenine.component.core.models.BasicUserInfoModel
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer


@Configuration
class BasicUserInfoArgumentResolver : HandlerMethodArgumentResolver {
  private val log = LogKt.getLog(BasicUserInfoArgumentResolver::class)
  override fun supportsParameter(parameter: MethodParameter): Boolean {
    return parameter.parameter.type == BasicUserInfoModel::class.java
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
