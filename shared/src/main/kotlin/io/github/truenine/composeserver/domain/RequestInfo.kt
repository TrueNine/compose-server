package io.github.truenine.composeserver.domain

/**
 * Basic user request context information.
 *
 * @param deviceId Device identifier
 * @param currentIpAddr Current request IP address
 * @author T_teng
 * @since 2023-04-06
 */
open class RequestInfo(open val deviceId: String? = null, open val currentIpAddr: String? = null) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is RequestInfo) return false
    if (deviceId != other.deviceId) return false
    if (currentIpAddr != other.currentIpAddr) return false
    return true
  }

  override fun hashCode(): Int {
    var result = deviceId?.hashCode() ?: 0
    result = 31 * result + (currentIpAddr?.hashCode() ?: 0)
    return result
  }

  override fun toString(): String {
    return "RequestInfo(deviceId=$deviceId, currentIpAddr=$currentIpAddr)"
  }
}
