package net.yan100.compose.core.ctx

import org.springframework.core.NamedInheritableThreadLocal

object UserInfoContextHolder {
  @JvmStatic
  private val CURRENT_USER: ThreadLocal<net.yan100.compose.core.models.RequestInfo> = NamedInheritableThreadLocal("UserInfoContextHolder::current_user")

  @JvmStatic
  fun set(info: net.yan100.compose.core.models.RequestInfo) {
    CURRENT_USER.set(info)
  }

  @JvmStatic
  fun get(): net.yan100.compose.core.models.RequestInfo? {
    return CURRENT_USER.get()
  }

  @JvmStatic
  fun clean() {
    CURRENT_USER.remove()
  }
}
