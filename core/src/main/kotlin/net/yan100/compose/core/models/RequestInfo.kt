package net.yan100.compose.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.annotation.Nullable

/**
 * 基础用户传递信息
 *
 * @author T_teng
 * @since 2023-04-06
 */
open class RequestInfo {
  @Nullable
  var userId: String? = null


  @Nullable
  var account: String? = null

  @Nullable
  @JsonIgnore
  var deviceId: String? = null

  @Nullable
  var loginIpAddr: String? = null

  @Nullable
  var currentIpAddr: String? = null

  override fun toString(): String {
    return mapOf(
      "userId" to userId,
      "account" to account,
      "currentIpAddr" to currentIpAddr,
      "loginIpAddr" to loginIpAddr,
      "deviceId" to deviceId
    ).toString()
  }
}
