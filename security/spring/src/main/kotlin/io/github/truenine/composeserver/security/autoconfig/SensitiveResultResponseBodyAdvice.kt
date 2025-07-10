package io.github.truenine.composeserver.security.autoconfig

import io.github.truenine.composeserver.Pr
import io.github.truenine.composeserver.annotations.SensitiveResponse
import io.github.truenine.composeserver.domain.ISensitivity
import io.github.truenine.composeserver.slf4j
import java.lang.reflect.ParameterizedType
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

private val log = slf4j<SensitiveResultResponseBodyAdvice>()

@ControllerAdvice
class SensitiveResultResponseBodyAdvice : ResponseBodyAdvice<Any> {
  private val supportAnnotationClassType = SensitiveResponse::class.java
  private val interfaceType = ISensitivity::class.java

  // TODO 实现 resolver ，缓存 每个不同对象的序列化规则，例如：{a: {b: Sensitive}}
  // TODO 可以实现 resolver ，规范化
  // TODO 加入缓存机制，同时考虑到动态加载
  override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
    val hasAnnotation = returnType.method?.isAnnotationPresent(supportAnnotationClassType) == true
    return hasAnnotation
  }

  fun getGenericType(returnType: MethodParameter, clazz: Class<*> = interfaceType): Class<*>? {
    if (returnType.genericParameterType is ParameterizedType) {
      val rawType = (returnType.genericParameterType as ParameterizedType).rawType
      if (rawType is Class<*> && rawType.isAssignableFrom(clazz)) return rawType
    }
    return null
  }

  fun isExtendTypeFor(returnType: MethodParameter, type: Class<*> = interfaceType): Boolean {
    return getGenericType(returnType) != null
  }

  override fun beforeBodyWrite(
    body: Any?,
    returnType: MethodParameter,
    selectedContentType: MediaType,
    selectedConverterType: Class<out HttpMessageConverter<*>>,
    request: ServerHttpRequest,
    response: ServerHttpResponse,
  ): Any? {
    when (body) {
      is ISensitivity -> body.changeWithSensitiveData()
      is Collection<*> -> body.forEach { if (it is ISensitivity) it.changeWithSensitiveData() }

      is Map<*, *> ->
        body.forEach {
          if (it.key is ISensitivity) (it.key as ISensitivity).changeWithSensitiveData()
          if (it.value is ISensitivity) (it.value as ISensitivity).changeWithSensitiveData()
        }

      is Array<*> -> body.forEach { if (it is ISensitivity) it.changeWithSensitiveData() }

      is Iterable<*> -> body.forEach { if (it is ISensitivity) it.changeWithSensitiveData() }

      is Iterator<*> -> body.forEach { if (it is ISensitivity) it.changeWithSensitiveData() }

      is Pr<*> -> {
        if (body.d.isNotEmpty()) {
          val b = body.d.firstOrNull()?.let { it is ISensitivity }
          if (b == true) {
            body.d.forEach { if (it is ISensitivity) it.changeWithSensitiveData() }
          }
        }
      }

      else -> {}
    }
    return body
  }
}
