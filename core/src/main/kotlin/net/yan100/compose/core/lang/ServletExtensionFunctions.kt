package net.yan100.compose.core.lang

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.yan100.compose.core.http.Headers
import net.yan100.compose.core.http.InterAddressUtil
import net.yan100.compose.core.http.MediaTypes
import java.io.OutputStream
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

/**
 * ## 当前请求的 ip 地址
 *
 * 尽量获取到真实的ip地址
 */
val HttpServletRequest.remoteRequestIp: String
  get() = InterAddressUtil.getRequestIpAddress(this)

/**
 * 获取当前设备的 deviceId
 */
val HttpServletRequest.deviceId: String
  get() = Headers.getDeviceId(this)

/**
 * ## 设置下载时的东西
 */
fun HttpServletResponse.withDownload(
  fileName: String,
  contentType: MediaTypes = MediaTypes.BINARY,
  charset: Charset = StandardCharsets.UTF_8,
  closeBlock: ((outputStream: OutputStream) -> Unit)?
) {
  this.setHeader(Headers.CONTENT_DISPOSITION, Headers.downloadDisposition(fileName, charset))
  this.setHeader(Headers.CONTENT_TYPE, contentType.getValue())
  this.characterEncoding = charset.displayName()
  closeBlock?.also { blockFn ->
    this.outputStream.use {
      blockFn(it)
    }
  }
}
