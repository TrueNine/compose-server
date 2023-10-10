package net.yan100.compose.core.lang

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.yan100.compose.core.http.MediaTypes
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

val HttpServletRequest.headerMap: Map<String, String>
  get() = headerNames.asSequence().map { it to getHeader(it) }.toMap()

val HttpServletResponse.headerMap: Map<String, String>
  get() = headerNames.asSequence().map { it to getHeader(it) }.toMap()

fun HttpServletResponse.useResponse(
  contentType: MediaTypes = MediaTypes.BINARY,
  charset: Charset = StandardCharsets.UTF_8,
  locale: Locale = Locale.CHINA,
  with: (HttpServletResponse) -> HttpServletResponse
): HttpServletResponse {
  this.contentType = contentType.getValue()
  this.characterEncoding = charset.displayName()
  this.locale = locale
  return with(this)
}

fun HttpServletResponse.useSse(
  charset: Charset = StandardCharsets.UTF_8, locale: Locale = Locale.CHINA, with: (HttpServletResponse) -> HttpServletResponse
): HttpServletResponse {
  return this.useResponse(contentType = MediaTypes.SSE, charset = charset, locale = locale) { with(it) }
}
