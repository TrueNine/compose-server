package io.github.truenine.composeserver.domain

import io.github.truenine.composeserver.RefId
import java.util.concurrent.CopyOnWriteArrayList

/**
 * User identity information required for security checks.
 *
 * @param deviceId Device identifier
 * @param currentIpAddr Current request IP address
 * @param account Account name
 * @param encryptedPassword Encrypted password
 * @param loginIpAddr Login IP address
 * @param loginPlatform Login platform
 * @param userId User identifier
 * @param nonLocked Whether the account is not locked
 * @param nonExpired Whether the account is not expired
 * @param enabled Whether the account is enabled
 * @param roles Role list
 * @param permissions Permission list
 * @param depts Department list
 * @author TrueNine
 * @since 2022-12-10
 */
data class AuthRequestInfo(
  val userId: RefId,
  val account: String,
  override val deviceId: String? = null,
  override val currentIpAddr: String? = null,
  val encryptedPassword: String? = null,
  val loginIpAddr: String? = null,
  val loginPlatform: String? = null,
  val nonLocked: Boolean = false,
  val nonExpired: Boolean = false,
  val enabled: Boolean = false,
  val roles: List<String> = CopyOnWriteArrayList(),
  val permissions: List<String> = CopyOnWriteArrayList(),
  val depts: List<String> = CopyOnWriteArrayList(),
) : RequestInfo() {
  /** ## Whether the user is fully logged in */
  val isLogin
    get() = userId != null && enabled && nonLocked && nonExpired && account != null
}
