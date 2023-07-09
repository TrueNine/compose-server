package net.yan100.compose.core.lang

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.yan100.compose.core.http.Headers
import net.yan100.compose.core.http.Methods

val HttpServletRequest.headerMap: Map<String, String>
  get() = headerNames.asSequence()
    .map { it to getHeader(it) }
    .toMap()

fun HttpServletResponse.allowCors(request: HttpServletRequest): HttpServletResponse {
  if (request.method == Methods.OPTIONS) {
    setHeader(Headers.CORS_ALLOW_ORIGIN, request.requestURL.toString())
    setHeader(Headers.CORS_ALLOW_CREDENTIALS, true.toString())
    setHeader(Headers.CORS_ALLOW_METHODS, "GET,POST,DELETE,PUT,HEAD,PATCH")
    setHeader(Headers.CORS_ALLOW_HEADERS, "*")
  }

  return this
}
