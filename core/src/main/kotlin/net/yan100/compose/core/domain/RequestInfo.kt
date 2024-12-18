package net.yan100.compose.core.domain

import net.yan100.compose.core.RefId

/**
 * 基础用户传递信息
 *
 * @author T_teng
 * @since 2023-04-06
 */
open class RequestInfo {
  var userId: RefId? = null
  var account: String? = null
  var deviceId: String? = null
  var loginIpAddr: String? = null
  var currentIpAddr: String? = null
}
