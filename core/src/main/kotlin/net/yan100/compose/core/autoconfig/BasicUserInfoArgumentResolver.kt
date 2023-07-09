package net.yan100.compose.core.autoconfig

import net.yan100.compose.core.ctx.UserInfoContextHolder
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.core.models.BasicUserInfoModel
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Component
class BasicUserInfoArgumentResolver : HandlerMethodArgumentResolver, WebMvcConfigurer {
  private val log = slf4j(BasicUserInfoArgumentResolver::class)

  override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
    super.addArgumentResolvers(resolvers)
    log.debug("注册用户信息拦ArgumentResolver")
    resolvers.add(this)
  }

  override fun supportsParameter(parameter: MethodParameter): Boolean {
    log.info("support by parameter = {}", parameter)
    return BasicUserInfoModel::class.java.isAssignableFrom(parameter.parameterType)
  }

  override fun resolveArgument(
    parameter: MethodParameter,
    mavContainer: ModelAndViewContainer?,
    webRequest: NativeWebRequest,
    binderFactory: WebDataBinderFactory?
  ): Any? {
    val u = UserInfoContextHolder.get()
    log.info("argument injection for {}", u)
    return u
  }
}
