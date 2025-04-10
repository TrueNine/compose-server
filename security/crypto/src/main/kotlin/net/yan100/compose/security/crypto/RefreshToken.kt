package net.yan100.compose.security.crypto

import net.yan100.compose.datetime

/**
 * 过期时间令牌
 *
 * @author TrueNine
 * @since 2022-12-20
 */
class RefreshToken {
  var userId: String? = null
  var deviceId: String? = null
  var loginIpAddr: String? = null
  var issueAt: datetime? = null
  var expireTime: datetime? = null
}
