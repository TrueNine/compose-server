package net.yan100.compose.security

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * 基础用户传递信息
 *
 * @author T_teng
 * @since 2023-04-06
 */
open class RequestInfo {
  lateinit var userId: String

  lateinit var account: String

  @JsonIgnore
  var deviceId: String? = null

  @JsonIgnore
  var loginIpAddr: String? = null

  @JsonIgnore
  var currentIpAddr: String? = null
}
