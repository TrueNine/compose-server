package net.yan100.compose.core.ctx

import org.springframework.core.NamedInheritableThreadLocal

object UserInfoContextHolder {
  @JvmStatic
  private val CURRENT_USER: ThreadLocal<net.yan100.compose.core.models.UserInfo> =
    NamedInheritableThreadLocal("UserInfoContextHolder::current_user")

  @JvmStatic
  fun set(info: net.yan100.compose.core.models.UserInfo) {
    CURRENT_USER.set(info)
  }

  @JvmStatic
  fun get(): net.yan100.compose.core.models.UserInfo? {
    return CURRENT_USER.get()
  }

  @JvmStatic
  fun clean() {
    CURRENT_USER.remove()
  }
}
