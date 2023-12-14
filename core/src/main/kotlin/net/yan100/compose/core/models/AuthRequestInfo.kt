package net.yan100.compose.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.annotation.Nullable
import net.yan100.compose.core.map.RequestInfoMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * security校验所需的用户身份
 *
 * @author TrueNine
 * @since 2022-12-10
 */
class AuthRequestInfo : RequestInfo() {
  @Nullable
  var encryptedPassword: String? = null

  @Nullable
  var nonLocked = false

  @Nullable
  var nonExpired = false

  @Nullable
  var enabled = false
  var roles: List<String> = CopyOnWriteArrayList()
  var permissions: List<String> = CopyOnWriteArrayList()
  var depts: List<String> = CopyOnWriteArrayList()

  @get:JsonIgnore
  val cleaned: AuthRequestInfo
    get() = RequestInfoMap.INSTANCE.clearAuthedInfo(this)
}
