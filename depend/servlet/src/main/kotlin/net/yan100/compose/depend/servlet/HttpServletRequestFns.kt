package net.yan100.compose.depend.servlet

import jakarta.servlet.http.HttpServletRequest
import net.yan100.compose.core.consts.IHeaders
import net.yan100.compose.core.consts.IInterAddr

val HttpServletRequest.headerMap: Map<String, String>
  get() = headerNames.asSequence().map { it to getHeader(it) }.toMap()

/** 获取当前设备的 deviceId */
val HttpServletRequest.deviceId: String?
  get() = IHeaders.getDeviceId(this)

/**
 * ## 当前请求的 ip 地址
 *
 * 尽量获取到真实的ip地址
 */
val HttpServletRequest.remoteRequestIp: String
  get() = IInterAddr.getRequestIpAddress(this)
