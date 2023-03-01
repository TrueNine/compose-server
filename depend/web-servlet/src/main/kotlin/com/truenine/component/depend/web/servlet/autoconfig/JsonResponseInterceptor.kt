package com.truenine.component.depend.web.servlet.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.api.http.R
import com.truenine.component.core.dev.BetaTest
import com.truenine.component.core.lang.KtLogBridge
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * spring 统一错误异常 json 返回
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@BetaTest
@RestController
@RequestMapping("error")
@ConditionalOnWebApplication
@ConditionalOnBean(
  DispatcherServlet::class
)
@Schema(hidden = true)
@Component
open class JsonResponseInterceptor(
  @Autowired
  private val objectMapper: ObjectMapper
) : WebMvcConfigurer,
  HandlerInterceptor {
  private var thatInterceptor: JsonResponseInterceptor = this

  init {
    log.info(
      "注册 json 返回类型转发器 = {}, mapper = {}",
      this::class.java,
      objectMapper
    )
    R.includeMapper(objectMapper)
  }

  override fun addInterceptors(registry: InterceptorRegistry) {
    registry.addInterceptor(thatInterceptor)
      .addPathPatterns("/**")
      .excludePathPatterns("/error/**")
  }

  override fun postHandle(
    request: HttpServletRequest,
    response: HttpServletResponse,
    handler: Any,
    modelAndView: ModelAndView?
  ) {
    if (response.status !in 0 until 400) {
      if (handler is HandlerMethod
        && handler.method.returnType == R::class.java
      ) {
        return
      }

      log.warn(
        "{} {} {} 被委派到转发错误",
        response.status,
        request.method.uppercase(),
        request.requestURI
      )
      modelAndView?.viewName = "forward:/error/" + response.status
    }
  }

  @RequestMapping("{code}")
  @ResponseBody
  @Operation(hidden = true)
  open fun error(
    @PathVariable("code") code: Int,
    response: HttpServletResponse
  ): R<*>? {
    response.status = code
    return R.failed(code)
  }


  companion object {
    private val log = KtLogBridge.getLog(JsonResponseInterceptor::class.java)
  }
}
