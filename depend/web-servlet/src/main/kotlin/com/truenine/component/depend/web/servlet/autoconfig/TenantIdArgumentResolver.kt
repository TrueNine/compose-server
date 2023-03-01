package com.truenine.component.depend.web.servlet.autoconfig

import com.truenine.component.core.api.http.ParameterNames
import com.truenine.component.core.dev.BetaTest
import com.truenine.component.core.lang.LogKt
import com.truenine.component.depend.web.servlet.annotations.CurrentTenant
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * jpa 租户拦截处理器
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@BetaTest
@Component
@ControllerAdvice
class TenantIdArgumentResolver : HandlerMethodArgumentResolver {
  init {
    log.info("注册租户id参数注入器")
  }

  override fun supportsParameter(parameter: MethodParameter): Boolean {
    return parameter.hasParameterAnnotation(CurrentTenant::class.java)
      && parameter.parameterType == String::class.java
  }

  override fun resolveArgument(
    parameter: MethodParameter,
    mavContainer: ModelAndViewContainer?,
    webRequest: NativeWebRequest,
    binderFactory: WebDataBinderFactory?
  ): Any? {
    return webRequest.getAttribute(ParameterNames.X_INTERNAL_TENANT_ID, 0)
  }

  companion object {
    @JvmStatic
    private val log = LogKt.getLog(TenantIdArgumentResolver::class)
  }
}
