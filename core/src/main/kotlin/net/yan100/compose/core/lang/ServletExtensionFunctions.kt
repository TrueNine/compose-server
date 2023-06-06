package net.yan100.compose.core.lang

import jakarta.servlet.http.HttpServletRequest

val HttpServletRequest.headerMap: Map<String, String>
  get() = headerNames.asSequence()
    .map { it to getHeader(it) }
    .toMap()


