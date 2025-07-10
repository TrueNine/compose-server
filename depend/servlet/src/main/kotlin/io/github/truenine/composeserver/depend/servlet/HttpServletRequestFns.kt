package io.github.truenine.composeserver.depend.servlet

import io.github.truenine.composeserver.consts.IHeaders
import io.github.truenine.composeserver.consts.IInterAddr
import jakarta.servlet.http.HttpServletRequest

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
