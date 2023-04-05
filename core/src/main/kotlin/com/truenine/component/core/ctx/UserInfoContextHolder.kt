package com.truenine.component.core.ctx

import com.truenine.component.core.models.BasicUserInfoModel
import org.springframework.core.NamedInheritableThreadLocal

object UserInfoContextHolder {
  @JvmStatic
  private val CURRENT_USER: ThreadLocal<BasicUserInfoModel> =
    NamedInheritableThreadLocal("UserInfoContextHolder::current_user")

  @JvmStatic
  fun set(info: BasicUserInfoModel) {
    CURRENT_USER.set(info)
  }

  @JvmStatic
  fun get(): BasicUserInfoModel? {
    return CURRENT_USER.get()
  }

  @JvmStatic
  fun clean() {
    CURRENT_USER.remove()
  }
}
