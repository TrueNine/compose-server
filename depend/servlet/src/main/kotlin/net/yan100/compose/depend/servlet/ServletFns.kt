/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
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
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.depend.servlet

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.yan100.compose.core.consts.IHeaders
import net.yan100.compose.core.consts.IInterAddr
import net.yan100.compose.core.typing.MimeTypes
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*

val HttpServletRequest.headerMap: Map<String, String>
  get() = headerNames.asSequence().map { it to getHeader(it) }.toMap()

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
  return this.useResponse(contentType = MimeTypes.SSE, charset = charset, locale = locale) { with(it) }
}

/**
 * ## 当前请求的 ip 地址
 *
 * 尽量获取到真实的ip地址
 */
val HttpServletRequest.remoteRequestIp: String
  get() = IInterAddr.getRequestIpAddress(this)

/** 获取当前设备的 deviceId */
val HttpServletRequest.deviceId: String
  get() = IHeaders.getDeviceId(this)

/** ## 设置下载时的东西 */
@Deprecated("流使用完毕就关了流")
fun HttpServletResponse.withDownload(
    fileName: String,
    contentType: MimeTypes = MimeTypes.BINARY,
    charset: Charset = Charsets.UTF_8,
    closeBlock: ((outputStream: OutputStream) -> Unit)?,
) {
  this.setHeader(IHeaders.CONTENT_DISPOSITION, IHeaders.downloadDisposition(fileName, charset))
  this.setHeader(IHeaders.CONTENT_TYPE, contentType.value)
  this.characterEncoding = charset.displayName()
  closeBlock?.also { blockFn -> this.outputStream.use { blockFn(it) } }
}
