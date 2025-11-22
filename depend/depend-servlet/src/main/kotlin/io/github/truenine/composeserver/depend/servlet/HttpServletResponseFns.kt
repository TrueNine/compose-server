package io.github.truenine.composeserver.depend.servlet

import io.github.truenine.composeserver.consts.IHeaders
import io.github.truenine.composeserver.enums.MediaTypes
import jakarta.servlet.http.HttpServletResponse
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*

val HttpServletResponse.headerMap: Map<String, String>
  get() = headerNames.asSequence().map { it to getHeader(it) }.toMap()

inline fun HttpServletResponse.useResponse(
  contentType: MediaTypes = MediaTypes.BINARY,
  charset: Charset = Charsets.UTF_8,
  locale: Locale = Locale.CHINA,
  crossinline with: (HttpServletResponse) -> HttpServletResponse,
): HttpServletResponse {
  this.contentType = contentType.value
  this.characterEncoding = charset.displayName()
  this.locale = locale
  return with(this)
}

inline fun HttpServletResponse.useSse(
  charset: Charset = Charsets.UTF_8,
  locale: Locale = Locale.CHINA,
  crossinline with: (HttpServletResponse) -> HttpServletResponse,
): HttpServletResponse {
  return this.useResponse(contentType = MediaTypes.SSE, charset = charset, locale = locale) { with(it) }
}

/** ## Set up for downloads */
@Deprecated("The stream is closed after use")
fun HttpServletResponse.withDownload(
  fileName: String,
  contentType: MediaTypes = MediaTypes.BINARY,
  charset: Charset = Charsets.UTF_8,
  closeBlock: ((outputStream: OutputStream) -> Unit)?,
) {
  this.setHeader(IHeaders.CONTENT_DISPOSITION, IHeaders.downloadDisposition(fileName, charset))
  this.setHeader(IHeaders.CONTENT_TYPE, contentType.value)
  this.characterEncoding = charset.displayName()
  closeBlock?.also { blockFn -> this.outputStream.use { blockFn(it) } }
}
