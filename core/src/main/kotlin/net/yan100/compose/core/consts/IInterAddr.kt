package net.yan100.compose.core.consts

import jakarta.servlet.http.HttpServletRequest
import java.net.InetAddress
import java.util.*
import java.util.stream.Collectors

/**
 * @author TrueNine
 * @since 2022-10-28
 */
interface IInterAddr {
  companion object {
    private fun isLocalHost(remoteHost: String): Boolean {
      if (LOCAL_HOST_IP.equals(remoteHost, ignoreCase = true)) return true
      if (LOCAL_HOST == remoteHost) return true
      return LOCAL_HOST_V6.equals(remoteHost, ignoreCase = true)
    }

    fun getRequestIpAddress(request: HttpServletRequest): String {
      var remoteAddress = request.remoteAddr
      if (isLocalHost(remoteAddress)) {
        try {
          val address = InetAddress.getLocalHost()
          remoteAddress = address.hostAddress
        } catch (ignore: Exception) {
          remoteAddress = LOCAL_HOST_IP
        }
      } else {
        val xRealIP = request.getHeader(IHeaders.X_REAL_IP)
        val xForwardedFor = request.getHeader(IHeaders.X_FORWARDED_FOR)
        if (xForwardedFor != null) {
          remoteAddress =
            xForwardedFor
              .split(",".toRegex())
              .dropLastWhile { it.isEmpty() }
              .toTypedArray()[0]
              .trim { it <= ' ' }
        } else if (xRealIP != null) {
          remoteAddress = xRealIP
        }
      }
      return remoteAddress
    }

    private val localHostName: String?
      get() {
        val hostName: String
        try {
          val addr = InetAddress.getLocalHost()
          hostName = addr.hostName
        } catch (ignore: Exception) {
          return null
        }
        return hostName
      }

    val allLocalHostIP: List<String>
      get() {
        val result: MutableList<String> = ArrayList()
        try {
          val hostName = localHostName
          if (hostName != null) {
            val addresses = InetAddress.getAllByName(hostName)
            result.addAll(
              Arrays.stream(addresses)
                .map { obj: InetAddress -> obj.hostAddress }
                .collect(Collectors.toSet())
            )
          }
        } catch (ignore: Exception) {
          return result
        }
        return result
      }

    private const val LOCAL_HOST_IP: String = "127.0.0.1"
    private const val LOCAL_HOST_V6: String = "0:0:0:0:0:0:0:1"
    private const val LOCAL_HOST: String = "localhost"
  }
}
