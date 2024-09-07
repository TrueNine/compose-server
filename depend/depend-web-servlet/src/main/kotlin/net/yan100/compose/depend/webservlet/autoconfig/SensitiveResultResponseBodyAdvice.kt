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

import net.yan100.compose.core.alias.Pr
import net.yan100.compose.core.annotations.SensitiveResponse
import net.yan100.compose.core.log.slf4j
import net.yan100.compose.core.models.sensitive.ISensitivity
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import java.lang.reflect.ParameterizedType

private val log = slf4j<SensitiveResultResponseBodyAdvice>()

@ControllerAdvice
class SensitiveResultResponseBodyAdvice : ResponseBodyAdvice<Any> {
  private val supportAnnotationClassType = SensitiveResponse::class.java
  private val interfaceType = ISensitivity::class.java

  // TODO 实现 resolver ，缓存 每个不同对象的序列化规则，例如：{a: {b: Sensitive}}
  // TODO 可以实现 resolver ，规范化
  // TODO 加入缓存机制，同时考虑到动态加载
  override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
    val hasAnnotation = returnType.method?.isAnnotationPresent(supportAnnotationClassType) ?: false
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
      is ISensitivity -> body.sensitive()
      is Collection<*> -> body.forEach { if (it is ISensitivity) it.sensitive() }
      is Map<*, *> ->
        body.forEach {
          if (it.key is ISensitivity) (it.key as ISensitivity).sensitive()
          if (it.value is ISensitivity) (it.value as ISensitivity).sensitive()
        }
      is Array<*> -> body.forEach { if (it is ISensitivity) it.sensitive() }
      is Iterable<*> -> body.forEach { if (it is ISensitivity) it.sensitive() }
      is Iterator<*> -> body.forEach { if (it is ISensitivity) it.sensitive() }
      is Pr<*> -> {
        if (body.dataList.isNotEmpty()) {
          val b = body.dataList.firstOrNull()?.let { it is ISensitivity }
          if (b == true) {
            body.dataList.forEach { if (it is ISensitivity) it.sensitive() }
          }
        }
      }
      else -> {}
    }
    return body
  }
}
