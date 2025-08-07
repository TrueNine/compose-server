package io.github.truenine.composeserver.depend.servlet.resolvers

import io.github.truenine.composeserver.domain.IPageParam
import io.github.truenine.composeserver.domain.IPageParamLike
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class IPageParamLikeArgumentResolver : HandlerMethodArgumentResolver {
  private val typeList = listOf(IPageParam::class.java, IPageParamLike::class.java)

  override fun supportsParameter(parameter: MethodParameter): Boolean {
    return parameter.parameterType in typeList
  }

  override fun resolveArgument(
    parameter: MethodParameter,
    mavContainer: ModelAndViewContainer?,
    webRequest: NativeWebRequest,
    binderFactory: WebDataBinderFactory?,
  ): Any? {
    val o = webRequest.getParameter("o")?.toIntOrNull()
    val s = webRequest.getParameter("s")?.toIntOrNull()
    val u = webRequest.getParameter("u")?.toBooleanStrictOrNull()
    return IPageParam[o, s, u]
  }
}
