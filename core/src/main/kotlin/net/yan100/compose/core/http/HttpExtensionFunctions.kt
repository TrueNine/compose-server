package net.yan100.compose.core.http

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * ## 当前请求的 ip 地址
 *
 * 尽量获取到真实的ip地址
 */
val HttpServletRequest.remoteRequestIp: String
  get() = InterAddressUtil.getRequestIpAddress(this)

/**
 * ## 设置下载时的东西
 */
fun HttpServletResponse.withDownload(fileName: String, contentType: MediaTypes = MediaTypes.BINARY, charset: Charset = StandardCharsets.UTF_8) {
  this.setHeader(Headers.CONTENT_DISPOSITION, Headers.downloadDisposition(fileName, charset))
  this.setHeader(Headers.CONTENT_TYPE, contentType.getValue())
  this.characterEncoding = charset.displayName()
}
