/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.depend.webservlet.extensionfunctions

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import net.yan100.compose.core.http.Headers
import net.yan100.compose.core.http.InterAddressUtil
import net.yan100.compose.core.typing.http.MediaTypes

val HttpServletRequest.headerMap: Map<String, String>
  get() = headerNames.asSequence().map { it to getHeader(it) }.toMap()

val HttpServletResponse.headerMap: Map<String, String>
  get() = headerNames.asSequence().map { it to getHeader(it) }.toMap()

inline fun HttpServletResponse.useResponse(
  contentType: MediaTypes = MediaTypes.BINARY,
  charset: Charset = StandardCharsets.UTF_8,
  locale: Locale = Locale.CHINA,
  crossinline with: (HttpServletResponse) -> HttpServletResponse
): HttpServletResponse {
  this.contentType = contentType.value
  this.characterEncoding = charset.displayName()
  this.locale = locale
  return with(this)
}

inline fun HttpServletResponse.useSse(
  charset: Charset = StandardCharsets.UTF_8,
  locale: Locale = Locale.CHINA,
  crossinline with: (HttpServletResponse) -> HttpServletResponse
): HttpServletResponse {
  return this.useResponse(contentType = MediaTypes.SSE, charset = charset, locale = locale) {
    with(it)
  }
}

/**
 * ## 当前请求的 ip 地址
 *
 * 尽量获取到真实的ip地址
 */
val HttpServletRequest.remoteRequestIp: String
  get() = InterAddressUtil.getRequestIpAddress(this)

/** 获取当前设备的 deviceId */
val HttpServletRequest.deviceId: String
  get() = Headers.getDeviceId(this)

/** ## 设置下载时的东西 */
fun HttpServletResponse.withDownload(
  fileName: String,
  contentType: MediaTypes = MediaTypes.BINARY,
  charset: Charset = StandardCharsets.UTF_8,
  closeBlock: ((outputStream: OutputStream) -> Unit)?
) {
  this.setHeader(Headers.CONTENT_DISPOSITION, Headers.downloadDisposition(fileName, charset))
  this.setHeader(Headers.CONTENT_TYPE, contentType.value)
  this.characterEncoding = charset.displayName()
  closeBlock?.also { blockFn -> this.outputStream.use { blockFn(it) } }
}
