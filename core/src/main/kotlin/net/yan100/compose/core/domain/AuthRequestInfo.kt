package net.yan100.compose.core.domain

import java.util.concurrent.CopyOnWriteArrayList

/**
 * security校验所需的用户身份
 *
 * @author TrueNine
 * @since 2022-12-10
 */
class AuthRequestInfo : RequestInfo() {
  var encryptedPassword: String? = null
  var nonLocked = false
  var nonExpired = false
  var enabled = false
  var roles: List<String> = CopyOnWriteArrayList()
  var permissions: List<String> = CopyOnWriteArrayList()
  var depts: List<String> = CopyOnWriteArrayList()
}
