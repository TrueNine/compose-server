package net.yan100.compose.depend.servlet

import jakarta.servlet.http.HttpServletResponse
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*
import net.yan100.compose.core.consts.IHeaders
import net.yan100.compose.core.typing.MimeTypes

val HttpServletResponse.headerMap: Map<String, String>
  get() = headerNames.asSequence().map { it to getHeader(it) }.toMap()

inline fun HttpServletResponse.useResponse(
  contentType: MimeTypes = MimeTypes.BINARY,
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
  return this.useResponse(
    contentType = MimeTypes.SSE,
    charset = charset,
    locale = locale,
  ) {
    with(it)
  }
}

/** ## 设置下载时的东西 */
@Deprecated("流使用完毕就关了流")
fun HttpServletResponse.withDownload(
  fileName: String,
  contentType: MimeTypes = MimeTypes.BINARY,
  charset: Charset = Charsets.UTF_8,
  closeBlock: ((outputStream: OutputStream) -> Unit)?,
) {
  this.setHeader(
    IHeaders.CONTENT_DISPOSITION,
    IHeaders.downloadDisposition(fileName, charset),
  )
  this.setHeader(IHeaders.CONTENT_TYPE, contentType.value)
  this.characterEncoding = charset.displayName()
  closeBlock?.also { blockFn -> this.outputStream.use { blockFn(it) } }
}
