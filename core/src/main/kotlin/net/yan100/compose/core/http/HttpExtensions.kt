package net.yan100.compose.core.http

import jakarta.servlet.http.HttpServletRequest

val HttpServletRequest.remoteRequestIp: String
  get() = InterAddressUtil.getRequestIpAddress(this)
