package io.github.truenine.composeserver.typing

enum class HTTPMethod(val methodName: String) : StringTyping {
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
    operator fun get(methodName: String?): HTTPMethod? = entries.firstOrNull { it.methodName == methodName?.uppercase() }
  }
}
