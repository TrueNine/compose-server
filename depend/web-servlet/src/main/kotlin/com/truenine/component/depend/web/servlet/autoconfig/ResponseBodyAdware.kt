package com.truenine.component.depend.web.servlet.autoconfig

import com.truenine.component.core.api.http.R
import com.truenine.component.core.api.http.Status
import com.truenine.component.core.lang.KtLogBridge
import com.truenine.component.core.spring.properties.ServletWebApplicationProperties
import org.springframework.core.MethodParameter
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

/**
 * 统一 json 返回处理器
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
open class ResponseBodyAdware(
  private val pps: ServletWebApplicationProperties
) : ResponseBodyAdvice<Any?> {

  init {
    log.info("注册 统一 json 返回处理器 = {}", this.javaClass.name)
  }

  override fun supports(
    returnType: MethodParameter,
    converterType: Class<out HttpMessageConverter<*>>
  ): Boolean {
    log.info("返回 = {} , {}", returnType, converterType)
    return !(pps.allowConverterClasses.contains(returnType.containingClass)
      || pps.allowConverters.contains(returnType.method?.name))
  }

  override fun beforeBodyWrite(
    body: Any?,
    returnType: MethodParameter,
    selectedContentType: MediaType,
    selectedConverterType: Class<out HttpMessageConverter<*>>,
    request: ServerHttpRequest,
    response: ServerHttpResponse
  ): Any? {
    log.info("返回类型 = {}", body?.javaClass)
    if (selectedContentType.equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
      if (body is R<*>) {
        response.setStatusCode(HttpStatus.valueOf(body.code))
      } else {
        val res = response as ServletServerHttpResponse
        val code = res.servletResponse.status
        if (code >= 400) {
          val errCode = Status.valueOf(code)
          return R.failed(body, errCode)
        }
        return R.successfully(body, Status.valueOf(code))
      }
    }
    return body
  }

  companion object {
    private val log =
      KtLogBridge.getLog(ServletWebApplicationProperties::class.java)
  }
}
