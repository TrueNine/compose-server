package io.github.truenine.composeserver.typing

import io.github.truenine.composeserver.IStringTyping

enum class HTTPMethod(val methodName: String) : IStringTyping {
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
    @JvmStatic operator fun get(methodName: String?): HTTPMethod? = entries.firstOrNull { it.methodName == methodName?.uppercase() }
  }
}
