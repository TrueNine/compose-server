package io.github.truenine.composeserver.security.crypto

import io.github.truenine.composeserver.datetime

/**
 * Refresh token with expiration metadata.
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
