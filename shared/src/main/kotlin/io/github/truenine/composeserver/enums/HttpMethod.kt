package io.github.truenine.composeserver.enums

import io.github.truenine.composeserver.IStringEnum

enum class HttpMethod(val methodName: String) : IStringEnum {
  GET("GET"),
  POST("POST"),
  PUT("PUT"),
  DELETE("DELETE"),
  PATCH("PATCH"),
  HEAD("HEAD"),
  OPTIONS("OPTIONS"),
  TRACE("TRACE"),
  CONNECT("CONNECT");

  override val value: String = methodName

  companion object {
    @JvmStatic operator fun get(methodName: String?): HttpMethod? = entries.firstOrNull { it.methodName == methodName?.uppercase() }
  }
}
