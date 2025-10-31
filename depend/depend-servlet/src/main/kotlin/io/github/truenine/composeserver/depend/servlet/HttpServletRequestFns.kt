package io.github.truenine.composeserver.depend.servlet

import io.github.truenine.composeserver.consts.IHeaders
import io.github.truenine.composeserver.consts.IInterAddr
import jakarta.servlet.http.HttpServletRequest

val HttpServletRequest.headerMap: Map<String, String>
  get() = headerNames.asSequence().map { it to getHeader(it) }.toMap()

/** Get the deviceId of the current device */
val HttpServletRequest.deviceId: String?
  get() = IHeaders.getDeviceId(this)

/**
 * ## IP address of the current request
 *
 * Tries to get the real IP address as much as possible
 */
val HttpServletRequest.remoteRequestIp: String
  get() = IInterAddr.getRequestIpAddress(this)
